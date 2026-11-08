package com.telecom.facturacion.debito.mapper;

import com.telecom.facturacion.debito.dto.DebitoAutomaticoResponse;
import com.telecom.facturacion.debito.entity.DebitoAutomatico;
import org.mapstruct.Mapper;

@Mapper
public interface DebitoAutomaticoMapper {

    DebitoAutomaticoResponse aDebitoAutomaticoResponse(DebitoAutomatico debito);
}
