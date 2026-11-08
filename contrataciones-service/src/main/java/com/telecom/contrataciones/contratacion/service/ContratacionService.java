package com.telecom.contrataciones.contratacion.service;

import com.telecom.contrataciones.catalogo.client.CatalogoClient;
import com.telecom.contrataciones.catalogo.dto.PlanDelCatalogo;
import com.telecom.contrataciones.contratacion.dto.ContratarPlanRequest;
import com.telecom.contrataciones.contratacion.entity.Contratacion;
import com.telecom.contrataciones.contratacion.exception.PlanYaContratadoException;
import com.telecom.contrataciones.contratacion.repository.ContratacionRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContratacionService {

    private final ContratacionRepository contratacionRepository;
    private final CatalogoClient catalogoClient;

    public ContratacionService(ContratacionRepository contratacionRepository, CatalogoClient catalogoClient) {
        this.contratacionRepository = contratacionRepository;
        this.catalogoClient = catalogoClient;
    }

    @Transactional
    public Contratacion contratar(String clienteEmail, ContratarPlanRequest request) {
        PlanDelCatalogo plan = catalogoClient.buscarPlan(request.planId());
        if (contratacionRepository.existsByClienteEmailAndPlanId(clienteEmail, plan.id())) {
            throw new PlanYaContratadoException(plan.nombre());
        }
        Contratacion contratacion = new Contratacion(
                clienteEmail,
                plan.id(),
                plan.nombre(),
                plan.precio(),
                request.direccion(),
                LocalDate.now());
        return contratacionRepository.save(contratacion);
    }
}
