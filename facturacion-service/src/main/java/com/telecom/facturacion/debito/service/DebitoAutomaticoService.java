package com.telecom.facturacion.debito.service;

import com.telecom.facturacion.debito.dto.ActivarDebitoRequest;
import com.telecom.facturacion.debito.entity.DebitoAutomatico;
import com.telecom.facturacion.debito.exception.DebitoNoActivoException;
import com.telecom.facturacion.debito.repository.DebitoAutomaticoRepository;
import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import com.telecom.facturacion.pago.service.PagoService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DebitoAutomaticoService {

    // Se cobran las pendientes y también las vencidas, para evitar la baja por falta de pago
    private static final List<EstadoFactura> A_COBRAR = List.of(EstadoFactura.PENDIENTE, EstadoFactura.VENCIDA);

    private final DebitoAutomaticoRepository debitoRepository;
    private final FacturaRepository facturaRepository;
    private final PagoService pagoService;

    public DebitoAutomaticoService(
            DebitoAutomaticoRepository debitoRepository,
            FacturaRepository facturaRepository,
            PagoService pagoService) {
        this.debitoRepository = debitoRepository;
        this.facturaRepository = facturaRepository;
        this.pagoService = pagoService;
    }

    // Si ya estaba activo, actualiza el medio de pago
    @Transactional
    public DebitoAutomatico activar(Long clienteId, ActivarDebitoRequest request) {
        DebitoAutomatico debito = debitoRepository.findByClienteId(clienteId)
                .orElseGet(() -> new DebitoAutomatico(clienteId, request.medio(), request.tokenDePago(), LocalDate.now()));
        debito.cambiarMedio(request.medio(), request.tokenDePago());
        return debitoRepository.save(debito);
    }

    @Transactional(readOnly = true)
    public DebitoAutomatico consultar(Long clienteId) {
        return debitoRepository.findByClienteId(clienteId).orElseThrow(DebitoNoActivoException::new);
    }

    @Transactional
    public void desactivar(Long clienteId) {
        debitoRepository.findByClienteId(clienteId).ifPresent(debitoRepository::delete);
    }

    // Lo corre el proceso diario. Un cobro rechazado deja la factura como estaba: se reintenta al día siguiente.
    @Transactional
    public int cobrarFacturasAdeudadas() {
        int cobradas = 0;
        for (DebitoAutomatico debito : debitoRepository.findAll()) {
            List<Factura> adeudadas = facturaRepository.findByClienteIdAndEstadoIn(debito.getClienteId(), A_COBRAR);
            for (Factura factura : adeudadas) {
                if (pagoService.intentarCobro(factura, debito.getMedio(), debito.getTokenDePago())) {
                    cobradas++;
                }
            }
        }
        return cobradas;
    }
}
