package com.noxas.reparaciones.dto.response;

import com.noxas.reparaciones.domain.EstadoConsulta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lo que se devuelve al cliente (confirmación) y lo que consume
 * el panel admin para listar/ver el detalle de una consulta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaResponseDTO {

    private Long id;
    private String numeroComprobante;
    private String nombreCliente;
    private String telefono;
    private String email;
    private String barrioNombre;
    private String direccion;
    private BigDecimal costoEstimado;
    private String equipo;
    private String descripcionProblema;
    private EstadoConsulta estado;
    private LocalDateTime creadoEn;
}