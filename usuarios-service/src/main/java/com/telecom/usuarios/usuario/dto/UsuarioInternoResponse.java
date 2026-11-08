package com.telecom.usuarios.usuario.dto;

import com.telecom.usuarios.usuario.entity.Rol;

// Datos que necesitan otros microservicios (nunca la contraseña)
public record UsuarioInternoResponse(
        Long id,
        String nombre,
        String email,
        Rol rol,
        boolean activo,
        String dni,
        String telefono) {
}
