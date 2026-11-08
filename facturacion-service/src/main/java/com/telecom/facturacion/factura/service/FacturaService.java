package com.telecom.facturacion.factura.service;

import com.telecom.facturacion.factura.entity.Factura;
import com.telecom.facturacion.factura.repository.FacturaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    // Las más recientes primero
    @Transactional(readOnly = true)
    public List<Factura> listarDelCliente(Long clienteId) {
        return facturaRepository.findByClienteIdOrderByFechaEmisionDesc(clienteId);
    }
}
