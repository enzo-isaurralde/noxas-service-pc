package com.noxas.reparaciones.mapper;

import com.noxas.reparaciones.domain.Consulta;
import com.noxas.reparaciones.dto.response.ConsultaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Solo mapea entidad -> DTO de respuesta. El armado de la entidad
 * a partir del request lo hace ConsultaService a mano, porque
 * necesita resolver el barrio contra la base y calcular el
 * numeroComprobante y el costoEstimado — eso no es un mapeo directo
 * de campos, así que no tiene sentido forzarlo en MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    // barrio es una relación, así que mapeamos explícitamente su nombre
    @Mapping(source = "barrio.nombre", target = "barrioNombre")
    ConsultaResponseDTO toResponseDTO(Consulta consulta);
}