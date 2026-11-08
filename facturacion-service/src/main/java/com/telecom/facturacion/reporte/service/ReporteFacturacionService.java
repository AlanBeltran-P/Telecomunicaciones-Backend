package com.telecom.facturacion.reporte.service;

import com.telecom.facturacion.debito.repository.DebitoAutomaticoRepository;
import com.telecom.facturacion.factura.entity.EstadoFactura;
import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.entity.MedioDePago;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import com.telecom.facturacion.reporte.dto.Agrupacion;
import com.telecom.facturacion.reporte.dto.CobradoPorMedio;
import com.telecom.facturacion.reporte.dto.FacturasPorEstado;
import com.telecom.facturacion.reporte.dto.MontosPorPeriodo;
import com.telecom.facturacion.reporte.dto.ReporteFacturacionResponse;
import com.telecom.facturacion.reporte.exception.RangoDeFechasInvalidoException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteFacturacionService {

    private final FacturaRepository facturaRepository;
    private final DebitoAutomaticoRepository debitoRepository;

    public ReporteFacturacionService(FacturaRepository facturaRepository, DebitoAutomaticoRepository debitoRepository) {
        this.facturaRepository = facturaRepository;
        this.debitoRepository = debitoRepository;
    }

    @Transactional(readOnly = true)
    public ReporteFacturacionResponse generar(LocalDate desde, LocalDate hasta, Agrupacion agrupacion) {
        if (desde.isAfter(hasta)) {
            throw new RangoDeFechasInvalidoException(desde, hasta);
        }
        List<Factura> emitidas = facturaRepository.findByFechaEmisionBetween(desde, hasta);
        List<Factura> pagadas = facturaRepository.findByFechaPagoBetween(desde, hasta);
        List<Factura> adeudadas = facturaRepository.findByEstadoIn(List.of(EstadoFactura.PENDIENTE, EstadoFactura.VENCIDA));

        return new ReporteFacturacionResponse(
                desde,
                hasta,
                sumar(emitidas),
                sumar(pagadas),
                sumar(adeudadas),
                contarPorEstado(emitidas),
                cobradoPorMedio(pagadas),
                debitoRepository.countByFechaAltaBetween(desde, hasta),
                debitoRepository.count(),
                serie(emitidas, pagadas, desde, hasta, agrupacion));
    }

    private BigDecimal sumar(List<Factura> facturas) {
        return facturas.stream().map(Factura::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private FacturasPorEstado contarPorEstado(List<Factura> facturas) {
        Map<EstadoFactura, Long> cantidades = facturas.stream()
                .collect(Collectors.groupingBy(Factura::getEstado, Collectors.counting()));
        return new FacturasPorEstado(
                cantidades.getOrDefault(EstadoFactura.PENDIENTE, 0L),
                cantidades.getOrDefault(EstadoFactura.PAGADA, 0L),
                cantidades.getOrDefault(EstadoFactura.VENCIDA, 0L));
    }

    // Los cuatro medios en el orden del enum, así el front les mantiene siempre el mismo color
    private List<CobradoPorMedio> cobradoPorMedio(List<Factura> pagadas) {
        Map<MedioDePago, List<Factura>> porMedio = pagadas.stream()
                .collect(Collectors.groupingBy(Factura::getMedioDePago));
        return Arrays.stream(MedioDePago.values())
                .map(medio -> {
                    List<Factura> delMedio = porMedio.getOrDefault(medio, List.of());
                    return new CobradoPorMedio(medio, delMedio.size(), sumar(delMedio));
                })
                .toList();
    }

    // Un punto por período, en cero si no hubo movimientos
    private List<MontosPorPeriodo> serie(
            List<Factura> emitidas, List<Factura> pagadas, LocalDate desde, LocalDate hasta, Agrupacion agrupacion) {
        Map<String, BigDecimal> facturadoPorPeriodo = sumarPorPeriodo(emitidas, Factura::getFechaEmision, agrupacion);
        Map<String, BigDecimal> cobradoPorPeriodo = sumarPorPeriodo(pagadas, Factura::getFechaPago, agrupacion);

        return Periodos.entre(desde, hasta, agrupacion).stream()
                .map(periodo -> new MontosPorPeriodo(
                        periodo,
                        facturadoPorPeriodo.getOrDefault(periodo, BigDecimal.ZERO),
                        cobradoPorPeriodo.getOrDefault(periodo, BigDecimal.ZERO)))
                .toList();
    }

    private Map<String, BigDecimal> sumarPorPeriodo(
            List<Factura> facturas, Function<Factura, LocalDate> fecha, Agrupacion agrupacion) {
        return facturas.stream()
                .collect(Collectors.groupingBy(
                        factura -> Periodos.claveDe(fecha.apply(factura), agrupacion),
                        Collectors.reducing(BigDecimal.ZERO, Factura::getMonto, BigDecimal::add)));
    }
}
