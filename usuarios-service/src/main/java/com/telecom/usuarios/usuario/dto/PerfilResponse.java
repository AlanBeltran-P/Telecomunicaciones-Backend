package com.telecom.usuarios.usuario.dto;

import com.telecom.usuarios.usuario.entity.Rol;

// Datos que cada usuario ve de sí mismo. DNI y teléfono quedan vacíos para el personal
public record PerfilResponse(Long id, String nombre, String email, Rol rol, String dni, String telefono) {
}
