package com.sgcae.sgcae_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "apoyos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apoyo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idApoyo;

    private String tipo;

    private Double monto;
}
