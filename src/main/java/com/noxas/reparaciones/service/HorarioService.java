package com.noxas.reparaciones.service;

import com.noxas.reparaciones.domain.DiaSemana;
import com.noxas.reparaciones.domain.HorarioAtencion;
import com.noxas.reparaciones.exception.FueraDeHorarioException;
import com.noxas.reparaciones.repository.HorarioAtencionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Valida que la fecha/hora que pide el cliente para la visita caiga
 * dentro de alguno de los horarios de atención activos.
 */
@Service
public class HorarioService {

    @Autowired
    private HorarioAtencionRepository horarioAtencionRepository;

    /**
     * Lanza FueraDeHorarioException si fechaHora no cae dentro de
     * ningún horario activo configurado para ese día de la semana.
     */
    public void validarHorario(LocalDateTime fechaHora) {

        DiaSemana diaSemana = convertirDiaSemana(fechaHora.getDayOfWeek());
        LocalTime hora = fechaHora.toLocalTime();

        List<HorarioAtencion> horariosDelDia = horarioAtencionRepository
                .findByDiaSemanaAndActivoTrue(diaSemana);

        boolean dentroDeAlgunHorario = horariosDelDia.stream()
                .anyMatch(h -> !hora.isBefore(h.getHoraInicio()) && !hora.isAfter(h.getHoraFin()));

        if (!dentroDeAlgunHorario) {
            throw new FueraDeHorarioException(
                    "El horario solicitado está fuera de nuestro horario de atención");
        }
    }

    // java.time.DayOfWeek viene en inglés; lo traducimos a nuestro enum en español
    private DiaSemana convertirDiaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }
}