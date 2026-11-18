package com.noxas.reparaciones.exception;

import com.noxas.reparaciones.dto.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Centraliza el manejo de errores de toda la API: en vez de que cada
 * excepción se traduzca sola (o termine en un 500 genérico de
 * Spring), acá se decide una única vez qué status HTTP y qué formato
 * de respuesta le corresponde a cada tipo de error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Horario fuera de atención -> 400 Bad Request
    @ExceptionHandler(FueraDeHorarioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarFueraDeHorario(FueraDeHorarioException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Barrio inválido/inactivo -> 400 Bad Request
    @ExceptionHandler(FueraDeCoberturaException.class)
    public ResponseEntity<ErrorResponseDTO> manejarFueraDeCobertura(FueraDeCoberturaException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Cualquier otro argumento inválido que se nos haya escapado -> 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> manejarIllegalArgument(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Se dispara cuando @Valid encuentra campos que no pasan las
     * validaciones del DTO (@NotBlank, @Email, @Future, etc.).
     * Junta todos los mensajes de error en uno solo, en vez de
     * devolver el objeto de error verboso que arma Spring por defecto.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensajes = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(". "));

        return construirRespuesta(HttpStatus.BAD_REQUEST, mensajes);
    }

    // Cualquier cosa no contemplada arriba -> 500, pero con el mismo formato
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGeneral(Exception ex) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intentá nuevamente.");
    }

    private ResponseEntity<ErrorResponseDTO> construirRespuesta(HttpStatus status, String mensaje) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje
        );
        return ResponseEntity.status(status).body(error);
    }
}