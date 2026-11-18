package com.noxas.reparaciones.repository;

import com.noxas.reparaciones.domain.BarrioCobertura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarrioCoberturaRepository extends JpaRepository<BarrioCobertura, Long> {

    // Para poblar el <select> del formulario, solo barrios activos
    List<BarrioCobertura> findByActivoTrueOrderByNombreAsc();
}