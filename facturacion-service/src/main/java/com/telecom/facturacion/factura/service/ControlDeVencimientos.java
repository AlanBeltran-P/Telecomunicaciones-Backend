package com.telecom.facturacion.factura.service;

import com.telecom.facturacion.contratacion.client.ContratacionesClient;
import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import com.telecom.facturacion.notificacion.service.NotificacionService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Historia 13: al vencer una factura se avisa al cliente; si a las 2 semanas sigue sin pagar,
// se da de baja el plan (queda suspendido) y se le avisa que se comunique con un empleado.
@Service
public class ControlDeVencimientos {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Logger log = LoggerFactory.getLogger(ControlDeVencimientos.class);

    private final FacturaRepository facturaRepository;
    private final ContratacionesClient contratacionesClient;
    private final NotificacionService notificacionService;
    private final int diasHastaLaBaja;

    public ControlDeVencimientos(
            FacturaRepository facturaRepository,
            ContratacionesClient contratacionesClient,
            NotificacionService notificacionService,
            @Value("${facturacion.dias-hasta-la-baja}") int diasHastaLaBaja) {
        this.facturaRepository = facturaRepository;
        this.contratacionesClient = contratacionesClient;
        this.notificacionService = notificacionService;
        this.diasHastaLaBaja = diasHastaLaBaja;
    }

    @Transactional
    public void controlarHasta(LocalDate hoy) {
        marcarVencidas(hoy);
        darDeBajaLasImpagas(hoy);
    }

    // Vence el día siguiente a la fecha de vencimiento
    private void marcarVencidas(LocalDate hoy) {
        for (Factura factura : facturaRepository.findByEstadoAndFechaVencimientoBefore(EstadoFactura.PENDIENTE, hoy)) {
            factura.vencer();
            LocalDate fechaDeBaja = factura.getFechaVencimiento().plusDays(diasHastaLaBaja);
            notificacionService.notificar(
                    factura.getClienteId(),
                    "Tu factura venció",
                    "La factura de " + factura.getPlanNombre() + " por $" + factura.getMonto()
                            + " venció el " + FECHA.format(factura.getFechaVencimiento())
                            + ". Pagala antes del " + FECHA.format(fechaDeBaja) + " para evitar la baja del servicio.");
        }
    }

    // Una sola baja por factura: se marca para no volver a pedirla al día siguiente
    private void darDeBajaLasImpagas(LocalDate hoy) {
        LocalDate vencidasAntesDe = hoy.minusDays(diasHastaLaBaja - 1L);
        for (Factura factura : facturaRepository.findByEstadoAndBajaSolicitadaFalseAndFechaVencimientoBefore(
                EstadoFactura.VENCIDA, vencidasAntesDe)) {
            try {
                darDeBaja(factura);
            } catch (RuntimeException error) {
                // Si contrataciones-service no responde, se reintenta al día siguiente
                log.error("No se pudo dar de baja la contratación {}", factura.getContratacionId(), error);
            }
        }
    }

    private void darDeBaja(Factura factura) {
        contratacionesClient.suspender(factura.getContratacionId());
        factura.marcarBajaSolicitada();
        notificacionService.notificar(
                factura.getClienteId(),
                "Dimos de baja tu plan",
                "Dimos de baja el plan " + factura.getPlanNombre() + " por la factura impaga del "
                        + FECHA.format(factura.getFechaVencimiento())
                        + ". Comunicate con un empleado para resolverlo.");
    }
}
