package com.telecom.usuarios.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // Guardada con BCrypt, nunca en texto plano
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    protected Usuario() {
        // Requerido por JPA
    }

    public Usuario(String email, String contrasena, Rol rol) {
        this.email = email;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Long getId() {
        return id;
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
}
