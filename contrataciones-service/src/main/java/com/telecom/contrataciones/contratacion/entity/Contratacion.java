package com.telecom.contrataciones.contratacion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contrataciones")
public class Contratacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clienteEmail;

    @Column(nullable = false)
    private Long planId;

    // Copia del catálogo al momento de contratar: si el plan cambia después, esto no cambia
    @Column(nullable = false)
    private String planNombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioMensual;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String dni;

    @Column(nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoContratacion estado;

    // La facturación empieza un mes después de esta fecha
    @Column(nullable = false)
    private LocalDate fechaContratacion;

    protected Contratacion() {
        // Requerido por JPA
    }

    public Contratacion(
            String clienteEmail,
            Long planId,
            String planNombre,
            BigDecimal precioMensual,
            String direccion,
            String dni,
            String telefono,
            LocalDate fechaContratacion) {
        this.clienteEmail = clienteEmail;
        this.planId = planId;
        this.planNombre = planNombre;
        this.precioMensual = precioMensual;
        this.direccion = direccion;
        this.dni = dni;
        this.telefono = telefono;
        this.fechaContratacion = fechaContratacion;
        this.estado = EstadoContratacion.PENDIENTE_INSTALACION;
    }

    public Long getId() {
        return id;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public Long getPlanId() {
        return planId;
    }

    public String getPlanNombre() {
        return planNombre;
    }

    public BigDecimal getPrecioMensual() {
        return precioMensual;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public EstadoContratacion getEstado() {
        return estado;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }
}
