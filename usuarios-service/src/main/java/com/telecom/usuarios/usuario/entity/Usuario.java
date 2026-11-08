package com.telecom.usuarios.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Admite vacío en la base porque la tabla ya tenía filas antes de este campo
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    // Guardada con BCrypt, nunca en texto plano
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // El default permite agregar la columna a una tabla que ya tiene filas
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean activo = true;

    // Solo para clientes: el personal no tiene DNI ni teléfono
    @Column(unique = true)
    private String dni;

    private String telefono;

    // Para los reportes de altas. Vacía en los usuarios creados antes de agregar este campo
    private LocalDate fechaAlta;

    protected Usuario() {
        // Requerido por JPA
    }

    // Personal (administrador, empleado, técnico)
    public Usuario(String nombre, String email, String contrasena, Rol rol) {
        this(nombre, email, contrasena, rol, null, null);
    }

    public Usuario(String nombre, String email, String contrasena, Rol rol, String dni, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.rol = rol;
        this.dni = dni;
        this.telefono = telefono;
        this.fechaAlta = LocalDate.now();
    }

    public void actualizarDatos(String nombre, String email, String dni, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.dni = dni;
        this.telefono = telefono;
    }

    public void cambiarRol(Rol rol) {
        this.rol = rol;
    }

    public void desactivar() {
        this.activo = false;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }
}
