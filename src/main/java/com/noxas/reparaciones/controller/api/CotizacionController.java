package com.noxas.reparaciones.controller.api;

import com.noxas.reparaciones.domain.BarrioCobertura;
import com.noxas.reparaciones.dto.response.CotizacionResponseDTO;
import com.noxas.reparaciones.repository.BarrioCoberturaRepository;
import com.noxas.reparaciones.service.CoberturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expone la cotización por barrio para que la landing pueda mostrar
 * el precio antes de que el cliente confirme la consulta.
 */
@RestController
@RequestMapping("/api/cobertura")
public class CotizacionController {

    @Autowired
    private BarrioCoberturaRepository barrioCoberturaRepository;

    @Autowired
    private CoberturaService coberturaService;

    /**
     * Lista los barrios activos, para poblar el <select> del formulario.
     */
    @GetMapping("/barrios")
    public ResponseEntity<List<BarrioCobertura>> listarBarrios() {
        List<BarrioCobertura> barrios = barrioCoberturaRepository.findByActivoTrueOrderByNombreAsc();
        return ResponseEntity.ok(barrios);
    }

    /**
     * Devuelve la cotización para el barrio elegido.
     */
    @GetMapping("/{barrioId}")
    public ResponseEntity<CotizacionResponseDTO> cotizar(@PathVariable Long barrioId) {
        CotizacionResponseDTO cotizacion = coberturaService.cotizarPorBarrio(barrioId);
        return ResponseEntity.ok(cotizacion);
    }
}