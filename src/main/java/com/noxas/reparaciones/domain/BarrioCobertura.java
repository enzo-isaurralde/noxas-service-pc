package com.noxas.reparaciones.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representa un barrio de CABA dentro de la zona de cobertura del
 * servicio a domicilio, con su costo fijo de visita.
 */
@Entity
@Table(name = "barrios_cobertura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarrioCobertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false)
    private Integer comuna;

    @Column(name = "costo_visita", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoVisita;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}