package com.telecom.contrataciones.reporte.service;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
import com.telecom.contrataciones.reporte.dto.Agrupacion;
import com.telecom.contrataciones.reporte.dto.CantidadPorNombre;
import com.telecom.contrataciones.reporte.dto.MovimientosPorPeriodo;
import com.telecom.contrataciones.reporte.dto.ReporteContratacionesResponse;
import com.telecom.contrataciones.reporte.exception.RangoDeFechasInvalidoException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteContratacionesService {

    private final ContratacionRepository contratacionRepository;

    public ReporteContratacionesService(ContratacionRepository contratacionRepository) {
        this.contratacionRepository = contratacionRepository;
    }

    @Transactional(readOnly = true)
    public ReporteContratacionesResponse generar(LocalDate desde, LocalDate hasta, Agrupacion agrupacion) {
        if (desde.isAfter(hasta)) {
            throw new RangoDeFechasInvalidoException(desde, hasta);
        }
        List<Contratacion> nuevas = contratacionRepository.findByFechaContratacionBetween(desde, hasta);
        List<Contratacion> bajas = contratacionRepository.findByFechaBajaBetween(desde, hasta);
        List<Contratacion> instaladas = contratacionRepository.findByFechaInstalacionBetween(desde, hasta);

        return new ReporteContratacionesResponse(
                desde,
                hasta,
                nuevas.size(),
                bajas.size(),
                contratacionRepository.findByFechaUltimaSuspensionBetween(desde, hasta).size(),
                contratacionRepository.findByFechaReactivacionBetween(desde, hasta).size(),
                instaladas.size(),
                contratacionRepository.countByEstado(EstadoContratacion.PENDIENTE_INSTALACION),
                contarPorNombre(nuevas, Contratacion::getPlanNombre),
                contarPorNombre(instaladas, Contratacion::getTecnicoNombre),
                serie(nuevas, bajas, instaladas, desde, hasta, agrupacion));
    }

    // De mayor a menor; las que no tienen nombre (ej. sin técnico) no se cuentan
    private List<CantidadPorNombre> contarPorNombre(
            List<Contratacion> contrataciones, Function<Contratacion, String> nombre) {
        return contrataciones.stream()
                .map(nombre)
                .filter(valor -> valor != null)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entrada -> new CantidadPorNombre(entrada.getKey(), entrada.getValue()))
                .sorted(Comparator.comparingLong(CantidadPorNombre::cantidad).reversed()
                        .thenComparing(CantidadPorNombre::nombre))
                .toList();
    }

    // Un punto por período, en cero si no hubo movimientos
    private List<MovimientosPorPeriodo> serie(
            List<Contratacion> nuevas,
            List<Contratacion> bajas,
            List<Contratacion> instaladas,
            LocalDate desde,
            LocalDate hasta,
            Agrupacion agrupacion) {
        Map<String, Long> altasPorPeriodo = contarPorPeriodo(nuevas, Contratacion::getFechaContratacion, agrupacion);
        Map<String, Long> bajasPorPeriodo = contarPorPeriodo(bajas, Contratacion::getFechaBaja, agrupacion);
        Map<String, Long> instalacionesPorPeriodo =
                contarPorPeriodo(instaladas, Contratacion::getFechaInstalacion, agrupacion);

        return Periodos.entre(desde, hasta, agrupacion).stream()
                .map(periodo -> new MovimientosPorPeriodo(
                        periodo,
                        altasPorPeriodo.getOrDefault(periodo, 0L),
                        bajasPorPeriodo.getOrDefault(periodo, 0L),
                        instalacionesPorPeriodo.getOrDefault(periodo, 0L)))
                .toList();
    }

    private Map<String, Long> contarPorPeriodo(
            List<Contratacion> contrataciones, Function<Contratacion, LocalDate> fecha, Agrupacion agrupacion) {
        return contrataciones.stream()
                .map(contratacion -> Periodos.claveDe(fecha.apply(contratacion), agrupacion))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
