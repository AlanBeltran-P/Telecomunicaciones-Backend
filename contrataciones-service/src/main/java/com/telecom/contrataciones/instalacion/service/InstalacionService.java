package com.telecom.contrataciones.instalacion.service;

import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
import com.telecom.contrataciones.contratacion.exception.ContratacionNoEncontradaException;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
import com.telecom.contrataciones.instalacion.exception.InstalacionDeOtroTecnicoException;
import com.telecom.contrataciones.instalacion.exception.InstalacionNoPendienteException;
import com.telecom.contrataciones.usuario.client.UsuariosClient;
import com.telecom.contrataciones.usuario.dto.UsuarioDelSistema;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstalacionService {

    private final ContratacionRepository contratacionRepository;
    private final UsuariosClient usuariosClient;

    public InstalacionService(ContratacionRepository contratacionRepository, UsuariosClient usuariosClient) {
        this.contratacionRepository = contratacionRepository;
        this.usuariosClient = usuariosClient;
    }

    // Las más antiguas primero: son las que llevan más tiempo esperando
    @Transactional(readOnly = true)
    public List<Contratacion> listarPendientes() {
        return contratacionRepository.findByEstadoOrderByFechaContratacionAsc(EstadoContratacion.PENDIENTE_INSTALACION);
    }

    @Transactional
    public Contratacion asignarTecnico(Long contratacionId, Long tecnicoId) {
        Contratacion contratacion = buscarPendiente(contratacionId);
        UsuarioDelSistema tecnico = usuariosClient.buscarTecnicoActivo(tecnicoId);
        contratacion.asignarTecnico(tecnico.id(), tecnico.nombre());
        return contratacion;
    }

    @Transactional(readOnly = true)
    public List<Contratacion> listarDelTecnico(Long tecnicoId) {
        return contratacionRepository.findByTecnicoIdAndEstadoOrderByFechaContratacionAsc(
                tecnicoId, EstadoContratacion.PENDIENTE_INSTALACION);
    }

    // Historial del técnico: las que ya instaló, la más reciente primero
    @Transactional(readOnly = true)
    public List<Contratacion> listarRealizadasDelTecnico(Long tecnicoId) {
        return contratacionRepository.findByTecnicoIdAndFechaInstalacionIsNotNullOrderByFechaInstalacionDesc(tecnicoId);
    }

    // Solo el técnico asignado puede marcarla como realizada
    @Transactional
    public Contratacion marcarRealizada(Long contratacionId, Long tecnicoId) {
        Contratacion contratacion = buscarPendiente(contratacionId);
        if (!contratacion.estaAsignadaA(tecnicoId)) {
            throw new InstalacionDeOtroTecnicoException(contratacionId);
        }
        contratacion.marcarInstalada(LocalDate.now());
        return contratacion;
    }

    private Contratacion buscarPendiente(Long contratacionId) {
        Contratacion contratacion = contratacionRepository.findById(contratacionId)
                .orElseThrow(() -> new ContratacionNoEncontradaException(contratacionId));
        if (!contratacion.estaPendienteDeInstalacion()) {
            throw new InstalacionNoPendienteException(contratacionId);
        }
        return contratacion;
    }
}
