package com.telecom.facturacion.factura.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;

// Una factura por contratación y por mes: la restricción única evita duplicados
@Entity
@Table(name = "facturas", uniqueConstraints = @UniqueConstraint(columnNames = {"contratacionId", "periodoDesde"}))
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Long contratacionId;

    @Column(nullable = false)
    private String planNombre;

    // En pesos: el precio mensual que quedó fijado al contratar
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    // Mes que cubre la factura
    @Column(nullable = false)
    private LocalDate periodoDesde;

    @Column(nullable = false)
    private LocalDate periodoHasta;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado;

    protected Factura() {
        // Requerido por JPA
    }

    public Factura(
            Long clienteId,
            Long contratacionId,
            String planNombre,
            BigDecimal monto,
            LocalDate periodoDesde,
            LocalDate periodoHasta,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento) {
        this.clienteId = clienteId;
        this.contratacionId = contratacionId;
        this.planNombre = planNombre;
        this.monto = monto;
        this.periodoDesde = periodoDesde;
        this.periodoHasta = periodoHasta;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = EstadoFactura.PENDIENTE;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getContratacionId() {
        return contratacionId;
    }

    public String getPlanNombre() {
        return planNombre;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDate getPeriodoDesde() {
        return periodoDesde;
    }

    public LocalDate getPeriodoHasta() {
        return periodoHasta;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public EstadoFactura getEstado() {
        return estado;
    }
}
