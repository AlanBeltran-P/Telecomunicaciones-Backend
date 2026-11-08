package com.telecom.facturacion.notificacion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// Aviso que el cliente ve dentro del sistema (además se le manda por email)
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private boolean leida;

    protected Notificacion() {
        // Requerido por JPA
    }

    public Notificacion(Long clienteId, String asunto, String mensaje, LocalDateTime fecha) {
        this.clienteId = clienteId;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.leida = false;
    }

    public void marcarLeida() {
        this.leida = true;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean isLeida() {
        return leida;
    }
}
