package com.telecom.facturacion.pago.service;

import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.entity.MedioDePago;
import com.telecom.facturacion.factura.exception.FacturaNoEncontradaException;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import com.telecom.facturacion.pago.client.PasarelaDePago;
import com.telecom.facturacion.pago.dto.PagarFacturaRequest;
import com.telecom.facturacion.pago.dto.ResultadoDelCobro;
import com.telecom.facturacion.pago.exception.FacturaYaPagadaException;
import com.telecom.facturacion.pago.exception.PagoRechazadoException;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoService {

    private final FacturaRepository facturaRepository;
    private final PasarelaDePago pasarelaDePago;

    public PagoService(FacturaRepository facturaRepository, PasarelaDePago pasarelaDePago) {
        this.facturaRepository = facturaRepository;
        this.pasarelaDePago = pasarelaDePago;
    }

    // Se puede pagar una factura pendiente o vencida; nunca una ya pagada
    @Transactional
    public Factura pagar(Long facturaId, Long clienteId, PagarFacturaRequest request) {
        Factura factura = facturaRepository.findByIdAndClienteId(facturaId, clienteId)
                .orElseThrow(() -> new FacturaNoEncontradaException(facturaId));
        if (factura.estaPagada()) {
            throw new FacturaYaPagadaException(facturaId);
        }
        if (!intentarCobro(factura, request.medio(), request.tokenDePago())) {
            throw new PagoRechazadoException(facturaId);
        }
        return factura;
    }

    // Cobra con la pasarela y, si se aprueba, deja la factura pagada. Lo usa también el débito automático.
    public boolean intentarCobro(Factura factura, MedioDePago medio, String tokenDePago) {
        String descripcion = "Factura " + factura.getId() + " - " + factura.getPlanNombre();
        ResultadoDelCobro resultado = pasarelaDePago.cobrar(medio, tokenDePago, factura.getMonto(), descripcion);
        if (resultado.aprobado()) {
            factura.registrarPago(medio, resultado.referencia(), LocalDate.now());
        }
        return resultado.aprobado();
    }
}
