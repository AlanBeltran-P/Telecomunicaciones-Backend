package com.telecom.facturacion.debito.entity;

import com.telecom.facturacion.factura.entity.MedioDePago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

// Si existe un registro para el cliente, el débito automático está activo
@Entity
@Table(name = "debitos_automaticos")
public class DebitoAutomatico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long clienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedioDePago medio;

    // Token reutilizable del proveedor para cobros recurrentes (nunca datos de tarjeta)
    @Column(nullable = false)
    private String tokenDePago;

    @Column(nullable = false)
    private LocalDate fechaAlta;

    protected DebitoAutomatico() {
        // Requerido por JPA
    }

    public DebitoAutomatico(Long clienteId, MedioDePago medio, String tokenDePago, LocalDate fechaAlta) {
        this.clienteId = clienteId;
        this.medio = medio;
        this.tokenDePago = tokenDePago;
        this.fechaAlta = fechaAlta;
    }

    public void cambiarMedio(MedioDePago medio, String tokenDePago) {
        this.medio = medio;
        this.tokenDePago = tokenDePago;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public MedioDePago getMedio() {
        return medio;
    }

    public String getTokenDePago() {
        return tokenDePago;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }
}
