package com.sgcae.sgcae_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCita;

    private String nombreCompleto;

    private String curp;

    private String colonia;

    private String telefono;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_apoyo")
    private Apoyo apoyo;

    private String fotoIneUrl;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private LocalDate fechaAsignada;

    private LocalTime horaAsignada;

    private LocalDate fechaRegistro = LocalDate.now();

    public enum Estado {
        PENDIENTE, EN_REPORTE, APROBADA, RECHAZADA, AGENDADA, COMPLETADA
    }
}
