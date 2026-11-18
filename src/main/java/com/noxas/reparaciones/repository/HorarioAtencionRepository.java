package com.noxas.reparaciones.repository;

import com.noxas.reparaciones.domain.DiaSemana;
import com.noxas.reparaciones.domain.HorarioAtencion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, Long> {

    // Trae los horarios activos configurados para un día puntual
    List<HorarioAtencion> findByDiaSemanaAndActivoTrue(DiaSemana diaSemana);
}