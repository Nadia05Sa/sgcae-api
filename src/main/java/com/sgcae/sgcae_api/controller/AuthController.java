package com.sgcae.sgcae_api.controller;

import com.sgcae.sgcae_api.dto.AuthResponse;
import com.sgcae.sgcae_api.dto.LoginRequest;
import com.sgcae.sgcae_api.security.JwtTokenProvider;
import com.sgcae.sgcae_api.service.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCorreo(),
                        loginRequest.getContrasena()));

        String token = jwtTokenProvider.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String nombre = userDetails.getNombre(); // asegúrate que exista este campo
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(new AuthResponse(token, nombre, rol));
    }

    @GetMapping("/me")
    public ResponseEntity<Collection<? extends GrantedAuthority>> quienSoy(Authentication authentication) {
        return ResponseEntity.ok(authentication.getAuthorities());
    }
}
