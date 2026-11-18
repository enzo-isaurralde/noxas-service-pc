package com.noxas.reparaciones.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita el procesamiento asíncrono en la app.
 * Los métodos anotados con @Async se ejecutan en un thread aparte,
 * sin bloquear el request HTTP principal.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // No hace falta configurar nada más: Spring usa un pool de threads por defecto
}