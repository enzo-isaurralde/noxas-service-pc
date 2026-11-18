package com.noxas.reparaciones.exception;

/**
 * Se lanza cuando el barrio elegido no existe o está inactivo
 * (fuera de la zona de cobertura del servicio).
 */
public class FueraDeCoberturaException extends RuntimeException {
    public FueraDeCoberturaException(String mensaje) {
        super(mensaje);
    }
}