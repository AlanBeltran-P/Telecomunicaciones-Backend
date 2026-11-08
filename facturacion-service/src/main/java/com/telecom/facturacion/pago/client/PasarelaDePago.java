package com.telecom.facturacion.pago.client;

import com.telecom.facturacion.factura.entity.MedioDePago;
import com.telecom.facturacion.pago.dto.ResultadoDelCobro;
import java.math.BigDecimal;

// Cobro con un proveedor externo. Para conectar Mercado Pago, PayPal o un procesador de tarjetas
// alcanza con una implementación nueva de esta interfaz: el resto del servicio no cambia.
public interface PasarelaDePago {

    ResultadoDelCobro cobrar(MedioDePago medio, String tokenDePago, BigDecimal monto, String descripcion);
}
