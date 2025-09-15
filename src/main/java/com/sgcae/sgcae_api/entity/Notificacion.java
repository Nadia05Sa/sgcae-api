package com.sgcae.sgcae_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private String mensaje;

    private LocalDateTime fecha = LocalDateTime.now();

    private boolean leido;

    @Enumerated(EnumType.STRING)
    private Rol paraRol;

    public enum Tipo {
        NUEVA_CITA, CITA_APROBADA
    }

    public enum Rol {
        ADMIN, SECRETARIA
    }
}
