package com.telecom.catalogo.plan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "planes")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    // Precio mensual en pesos
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private boolean activo = true;

    protected Plan() {
        // Requerido por JPA
    }

    public Plan(String nombre, String descripcion, BigDecimal precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public void actualizar(String nombre, String descripcion, BigDecimal precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public void darDeBaja() {
        this.activo = false;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public boolean isActivo() {
        return activo;
    }
}
