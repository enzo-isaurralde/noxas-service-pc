package com.noxas.reparaciones.exception;

/**
 * Se lanza cuando el cliente pide una visita fuera del horario
 * de atención configurado en horarios_atencion.
 */
public class FueraDeHorarioException extends RuntimeException {
    public FueraDeHorarioException(String mensaje) {
        super(mensaje);
    }
}