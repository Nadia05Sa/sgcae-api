package com.sgcae.sgcae_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Login y /me públicos
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/me").permitAll()

                        // ✅ Acceso público a imágenes
                        .requestMatchers("/archivos/**").permitAll()

                        // 📬 Notificaciones
                        .requestMatchers(HttpMethod.GET, "/notificaciones/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARIA")
                        .requestMatchers(HttpMethod.POST, "/notificaciones/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARIA", "ROLE_RECEPCION")
                        .requestMatchers(HttpMethod.PUT, "/notificaciones/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARIA")
                        .requestMatchers(HttpMethod.DELETE, "/notificaciones/**")
                        .hasAuthority("ROLE_ADMIN")

                        // 👤 Usuarios (solo admin)
                        .requestMatchers("/usuarios/**")
                        .hasAuthority("ROLE_ADMIN")

                        // 📝 Reportes
                        .requestMatchers(HttpMethod.GET, "/reportes/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARIA")
                        .requestMatchers(HttpMethod.POST, "/reportes/**")
                        .hasAnyAuthority("ROLE_SECRETARIA", "ROLE_RECEPCION")
                        .requestMatchers(HttpMethod.DELETE, "/reportes/**")
                        .hasAuthority("ROLE_ADMIN")

                        // 📅 Citas (orden importante)
                        .requestMatchers(HttpMethod.PUT, "/citas/marcar-en-reporte")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARIA", "ROLE_RECEPCION")

                        .requestMatchers(HttpMethod.GET, "/citas/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARIA", "ROLE_RECEPCION")
                        .requestMatchers(HttpMethod.POST, "/citas/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARIA", "ROLE_RECEPCION")

                        // ⚠️ Aquí se deja SOLO lo que no sea "/marcar-en-reporte"
                        .requestMatchers(HttpMethod.PUT, "/citas/**")
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/citas/**")
                        .hasAuthority("ROLE_ADMIN")

                        // 💰 Apoyos
                        .requestMatchers(HttpMethod.GET, "/apoyos/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARIA")
                        .requestMatchers(HttpMethod.POST, "/apoyos/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/apoyos/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/apoyos/**")
                        .hasAuthority("ROLE_ADMIN")

                        // 🛡️ Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
