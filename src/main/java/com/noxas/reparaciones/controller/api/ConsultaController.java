package com.noxas.reparaciones.controller.api;

import com.noxas.reparaciones.dto.request.ConsultaRequestDTO;
import com.noxas.reparaciones.dto.response.ConsultaResponseDTO;
import com.noxas.reparaciones.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Recibe el formulario de la landing y crea la consulta.
 */
@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    /**
     * @Valid dispara las validaciones del ConsultaRequestDTO (@NotBlank,
     * @Email, @Future, etc.) antes de que el método siquiera se ejecute.
     * Si algo falla acá, Spring responde 400 automáticamente (con un
     * mensaje genérico hasta que sumemos el GlobalExceptionHandler).
     */
    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> crear(@Valid @RequestBody ConsultaRequestDTO request) {
        ConsultaResponseDTO response = consultaService.crearConsulta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}