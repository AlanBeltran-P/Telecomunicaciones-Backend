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

    // Id del usuario en usuarios-service (no cambia aunque el cliente edite su email)
    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Long planId;

    // Copia del catálogo al momento de contratar: si el plan cambia después, esto no cambia
    @Column(nullable = false)
    private String planNombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioMensual;

    @Column(nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoContratacion estado;

    // La facturación empieza un mes después de esta fecha
    @Column(nullable = false)
    private LocalDate fechaContratacion;

    private LocalDate fechaBaja;

    // Técnico que hace la instalación: lo elige un empleado
    private Long tecnicoId;

    private String tecnicoNombre;

    private LocalDate fechaInstalacion;

    protected Contratacion() {
        // Requerido por JPA
    }

    public Contratacion(
            Long clienteId,
            Long planId,
            String planNombre,
            BigDecimal precioMensual,
            String direccion,
            LocalDate fechaContratacion) {
        this.clienteId = clienteId;
        this.planId = planId;
        this.planNombre = planNombre;
        this.precioMensual = precioMensual;
        this.direccion = direccion;
        this.fechaContratacion = fechaContratacion;
        this.estado = EstadoContratacion.PENDIENTE_INSTALACION;
    }

    public void darDeBaja(LocalDate fecha) {
        this.estado = EstadoContratacion.BAJA;
        this.fechaBaja = fecha;
    }

    public boolean estaDadaDeBaja() {
        return estado == EstadoContratacion.BAJA;
    }

    public boolean estaPendienteDeInstalacion() {
        return estado == EstadoContratacion.PENDIENTE_INSTALACION;
    }

    // Con la instalación hecha, el cliente ya recibe el servicio
    public void marcarInstalada(LocalDate fecha) {
        this.estado = EstadoContratacion.ACTIVA;
        this.fechaInstalacion = fecha;
    }

    public boolean estaAsignadaA(Long tecnicoId) {
        return tecnicoId.equals(this.tecnicoId);
    }

    // Mientras siga pendiente, el empleado puede cambiar de técnico
    public void asignarTecnico(Long tecnicoId, String tecnicoNombre) {
        this.tecnicoId = tecnicoId;
        this.tecnicoNombre = tecnicoNombre;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
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

    public EstadoContratacion getEstado() {
        return estado;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public String getTecnicoNombre() {
        return tecnicoNombre;
    }

    public LocalDate getFechaInstalacion() {
        return fechaInstalacion;
    }
}
