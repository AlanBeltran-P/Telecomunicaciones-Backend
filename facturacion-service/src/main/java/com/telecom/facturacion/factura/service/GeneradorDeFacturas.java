package com.telecom.facturacion.factura.service;

import com.telecom.facturacion.contratacion.client.ContratacionesClient;
import com.telecom.facturacion.contratacion.dto.ContratacionFacturable;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Regla: la factura número n cubre desde (contratación + n-1 meses) hasta (contratación + n meses)
// y se emite al terminar ese mes. Así, la primera llega un mes después de contratar.
@Service
public class GeneradorDeFacturas {

    private final ContratacionesClient contratacionesClient;
    private final FacturaRepository facturaRepository;
    private final int diasParaVencer;

    public GeneradorDeFacturas(
            ContratacionesClient contratacionesClient,
            FacturaRepository facturaRepository,
            @Value("${facturacion.dias-para-vencer}") int diasParaVencer) {
        this.contratacionesClient = contratacionesClient;
        this.facturaRepository = facturaRepository;
        this.diasParaVencer = diasParaVencer;
    }

    // Genera todas las facturas que ya corresponden hasta 'hoy'. Si un día no corrió,
    // al día siguiente se ponen al día; y correrlo dos veces no duplica nada.
    @Transactional
    public int generarHasta(LocalDate hoy) {
        int generadas = 0;
        for (ContratacionFacturable contratacion : contratacionesClient.listarFacturables()) {
            generadas += generarFaltantes(contratacion, hoy);
        }
        return generadas;
    }

    private int generarFaltantes(ContratacionFacturable contratacion, LocalDate hoy) {
        int generadas = 0;
        int mes = 1;
        // Se calcula siempre desde la fecha de contratación para que no se corra el día (ej. 31/01 → 28/02 → 31/03)
        LocalDate hasta = contratacion.fechaContratacion().plusMonths(mes);
        while (!hasta.isAfter(hoy)) {
            LocalDate desde = contratacion.fechaContratacion().plusMonths(mes - 1);
            boolean corresponde = !desde.isBefore(contratacion.facturarDesde());
            if (corresponde && !facturaRepository.existsByContratacionIdAndPeriodoDesde(contratacion.id(), desde)) {
                facturaRepository.save(nuevaFactura(contratacion, desde, hasta));
                generadas++;
            }
            mes++;
            hasta = contratacion.fechaContratacion().plusMonths(mes);
        }
        return generadas;
    }

    private Factura nuevaFactura(ContratacionFacturable contratacion, LocalDate desde, LocalDate hasta) {
        return new Factura(
                contratacion.clienteId(),
                contratacion.id(),
                contratacion.planNombre(),
                contratacion.precioMensual(),
                desde,
                hasta,
                hasta,
                hasta.plusDays(diasParaVencer));
    }
}
