package com.sgcae.sgcae_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReporte;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private LocalDateTime fechaGeneracion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "generado_por")
    private Usuario generadoPor;
}
