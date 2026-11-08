package com.telecom.facturacion.factura.mapper;

import com.telecom.facturacion.factura.dto.FacturaResponse;
import com.telecom.facturacion.factura.entity.Factura;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface FacturaMapper {

    FacturaResponse aFacturaResponse(Factura factura);

    List<FacturaResponse> aFacturaResponses(List<Factura> facturas);
}
