package com.telecom.contrataciones.instalacion.mapper;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.instalacion.dto.InstalacionResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InstalacionMapper {

    @Mapping(target = "contratacionId", source = "id")
    InstalacionResponse aInstalacionResponse(Contratacion contratacion);

    List<InstalacionResponse> aInstalacionResponses(List<Contratacion> contrataciones);
}
