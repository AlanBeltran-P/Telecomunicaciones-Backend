package com.telecom.usuarios.usuario.dto;

import com.telecom.usuarios.usuario.entity.Rol;

public record UsuarioResponse(Long id, String nombre, String email, Rol rol, boolean activo) {
}
