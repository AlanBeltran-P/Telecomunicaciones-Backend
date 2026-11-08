package com.telecom.facturacion.pago.client;

import com.telecom.facturacion.factura.entity.MedioDePago;
import com.telecom.facturacion.pago.dto.ResultadoDelCobro;
import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Pasarela de desarrollo: no cobra de verdad. Aprueba siempre, salvo el token "rechazado",
// para poder probar los dos casos. Reemplazar por las integraciones reales cuando haya credenciales.
@Component
public class PasarelaSimulada implements PasarelaDePago {

    public static final String TOKEN_QUE_SE_RECHAZA = "rechazado";

    private static final Logger log = LoggerFactory.getLogger(PasarelaSimulada.class);

    @Override
    public ResultadoDelCobro cobrar(MedioDePago medio, String tokenDePago, BigDecimal monto, String descripcion) {
        if (TOKEN_QUE_SE_RECHAZA.equals(tokenDePago)) {
            log.info("Cobro simulado RECHAZADO: {} ${} ({})", medio, monto, descripcion);
            return ResultadoDelCobro.rechazado();
        }
        String referencia = "SIM-" + UUID.randomUUID();
        log.info("Cobro simulado aprobado: {} ${} ({}) ref {}", medio, monto, descripcion, referencia);
        return ResultadoDelCobro.aprobado(referencia);
    }
}
