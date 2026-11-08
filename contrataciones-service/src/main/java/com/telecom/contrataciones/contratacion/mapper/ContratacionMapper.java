package com.telecom.contrataciones.contratacion.mapper;

import com.telecom.contrataciones.contratacion.dto.ContratacionResponse;
import com.telecom.contrataciones.contratacion.entity.Contratacion;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface ContratacionMapper {

    ContratacionResponse aContratacionResponse(Contratacion contratacion);

    List<ContratacionResponse> aContratacionResponses(List<Contratacion> contrataciones);
}
