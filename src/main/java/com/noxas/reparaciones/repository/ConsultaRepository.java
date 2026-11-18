package com.noxas.reparaciones.repository;

import com.noxas.reparaciones.domain.Consulta;
import com.noxas.reparaciones.domain.EstadoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Para el panel admin: listar filtrando por estado (ej. solo PENDIENTE)
    List<Consulta> findByEstadoOrderByCreadoEnDesc(EstadoConsulta estado);

    // Listado general del panel, más recientes primero
    List<Consulta> findAllByOrderByCreadoEnDesc();

    // Chequeo de unicidad al generar el número de comprobante
    boolean existsByNumeroComprobante(String numeroComprobante);
}