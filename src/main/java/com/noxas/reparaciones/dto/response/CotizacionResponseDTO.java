package com.noxas.reparaciones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CotizacionResponseDTO {
    private String barrioNombre;
    private BigDecimal costoEstimado;
    private boolean gratis;
}