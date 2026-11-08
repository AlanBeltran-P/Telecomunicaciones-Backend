package com.telecom.usuarios.reporte.service;

import com.telecom.usuarios.reporte.dto.Agrupacion;
import com.telecom.usuarios.reporte.dto.CantidadPorPeriodo;
import com.telecom.usuarios.reporte.dto.PersonalPorRol;
import com.telecom.usuarios.reporte.dto.ReporteUsuariosResponse;
import com.telecom.usuarios.reporte.exception.RangoDeFechasInvalidoException;
import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteUsuariosService {

    private final UsuarioRepository usuarioRepository;

    public ReporteUsuariosService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public ReporteUsuariosResponse generar(LocalDate desde, LocalDate hasta, Agrupacion agrupacion) {
        if (desde.isAfter(hasta)) {
            throw new RangoDeFechasInvalidoException(desde, hasta);
        }
        List<Usuario> clientesNuevos = usuarioRepository.findByRolAndFechaAltaBetween(Rol.CLIENTE, desde, hasta);
        return new ReporteUsuariosResponse(
                desde,
                hasta,
                clientesNuevos.size(),
                usuarioRepository.countByRolAndActivoTrue(Rol.CLIENTE),
                personalActivo(),
                altasPorPeriodo(clientesNuevos, desde, hasta, agrupacion));
    }

    private PersonalPorRol personalActivo() {
        return new PersonalPorRol(
                usuarioRepository.countByRolAndActivoTrue(Rol.ADMINISTRADOR),
                usuarioRepository.countByRolAndActivoTrue(Rol.EMPLEADO),
                usuarioRepository.countByRolAndActivoTrue(Rol.TECNICO));
    }

    // Un punto por período, en cero si no hubo altas
    private List<CantidadPorPeriodo> altasPorPeriodo(
            List<Usuario> clientes, LocalDate desde, LocalDate hasta, Agrupacion agrupacion) {
        Map<String, Long> altas = clientes.stream()
                .map(cliente -> Periodos.claveDe(cliente.getFechaAlta(), agrupacion))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return Periodos.entre(desde, hasta, agrupacion).stream()
                .map(periodo -> new CantidadPorPeriodo(periodo, altas.getOrDefault(periodo, 0L)))
                .toList();
    }
}
