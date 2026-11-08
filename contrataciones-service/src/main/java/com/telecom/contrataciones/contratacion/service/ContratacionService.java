package com.telecom.contrataciones.contratacion.service;

import com.telecom.contrataciones.catalogo.client.CatalogoClient;
import com.telecom.contrataciones.catalogo.dto.PlanDelCatalogo;
import com.telecom.contrataciones.contratacion.dto.ContratarPlanRequest;
import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.entity.EstadoContratacion;
import com.telecom.contrataciones.contratacion.exception.ContratacionNoEncontradaException;
import com.telecom.contrataciones.contratacion.exception.ContratacionYaDadaDeBajaException;
import com.telecom.contrataciones.contratacion.exception.PlanYaContratadoException;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
import com.telecom.contrataciones.usuario.client.UsuariosClient;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContratacionService {

    private final ContratacionRepository contratacionRepository;
    private final CatalogoClient catalogoClient;
    private final UsuariosClient usuariosClient;

    public ContratacionService(
            ContratacionRepository contratacionRepository,
            CatalogoClient catalogoClient,
            UsuariosClient usuariosClient) {
        this.contratacionRepository = contratacionRepository;
        this.catalogoClient = catalogoClient;
        this.usuariosClient = usuariosClient;
    }

    @Transactional
    public Contratacion contratar(Long clienteId, ContratarPlanRequest request) {
        PlanDelCatalogo plan = catalogoClient.buscarPlan(request.planId());
        // Un plan dado de baja se puede volver a contratar
        boolean yaLoTiene = contratacionRepository.existsByClienteIdAndPlanIdAndEstadoNot(
                clienteId, plan.id(), EstadoContratacion.BAJA);
        if (yaLoTiene) {
            throw new PlanYaContratadoException(plan.nombre());
        }
        Contratacion contratacion = new Contratacion(
                clienteId,
                plan.id(),
                plan.nombre(),
                plan.precio(),
                request.direccion(),
                LocalDate.now());
        return contratacionRepository.save(contratacion);
    }

    // Un empleado contrata por un cliente: primero confirma que el cliente existe y está activo
    @Transactional
    public Contratacion contratarParaCliente(Long clienteId, ContratarPlanRequest request) {
        usuariosClient.buscarClienteActivo(clienteId);
        return contratar(clienteId, request);
    }

    @Transactional(readOnly = true)
    public List<Contratacion> listarDelCliente(Long clienteId) {
        return contratacionRepository.findByClienteIdOrderByFechaContratacionDesc(clienteId);
    }

    @Transactional
    public Contratacion darDeBaja(Long id) {
        Contratacion contratacion = contratacionRepository.findById(id)
                .orElseThrow(() -> new ContratacionNoEncontradaException(id));
        if (contratacion.estaDadaDeBaja()) {
            throw new ContratacionYaDadaDeBajaException(id);
        }
        contratacion.darDeBaja(LocalDate.now());
        return contratacion;
    }
}
