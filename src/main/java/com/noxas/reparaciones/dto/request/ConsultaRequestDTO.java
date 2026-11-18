package com.noxas.reparaciones.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Datos que llegan desde el formulario de la landing para crear
 * una consulta. barrioId referencia al barrio ya cotizado.
 */
@Getter
@Setter
public class ConsultaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCliente;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Email(message = "El email no es válido")
    private String email;

    @NotNull(message = "Debés seleccionar un barrio")
    private Long barrioId;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private String equipo;

    @NotBlank(message = "Contanos qué problema tiene el equipo")
    private String descripcionProblema;

    // Horario en el que el cliente prefiere que vayas
    @NotNull(message = "Debés indicar fecha y hora preferida para la visita")
    @Future(message = "La fecha debe ser a futuro")
    private LocalDateTime fechaHoraSolicitada;
}