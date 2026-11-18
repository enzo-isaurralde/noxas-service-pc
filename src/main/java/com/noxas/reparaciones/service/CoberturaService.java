package com.noxas.reparaciones.service;

import com.noxas.reparaciones.domain.BarrioCobertura;
import com.noxas.reparaciones.dto.response.CotizacionResponseDTO;
import com.noxas.reparaciones.exception.FueraDeCoberturaException;
import com.noxas.reparaciones.repository.BarrioCoberturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Resuelve la cotización de una visita a partir del barrio elegido
 * por el cliente. El precio ya está fijado por barrio en la tabla
 * barrios_cobertura, así que acá no hay cálculo de distancia.
 */
@Service
public class CoberturaService {

    @Autowired
    private BarrioCoberturaRepository barrioCoberturaRepository;

    /**
     * Busca el barrio por id y arma la respuesta de cotización.
     * Lanza IllegalArgumentException si el barrio no existe o está inactivo.
     */
    public CotizacionResponseDTO cotizarPorBarrio(Long barrioId) {

        BarrioCobertura barrio = barrioCoberturaRepository.findById(barrioId)
                .filter(BarrioCobertura::getActivo)
                .orElseThrow(() -> new FueraDeCoberturaException(
                        "El barrio seleccionado no está disponible"));

        boolean esGratis = barrio.getCostoVisita().compareTo(BigDecimal.ZERO) == 0;

        return new CotizacionResponseDTO(barrio.getNombre(), barrio.getCostoVisita(), esGratis);
    }
}