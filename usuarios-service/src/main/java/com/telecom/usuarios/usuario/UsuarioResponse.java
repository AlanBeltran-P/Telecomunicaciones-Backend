package com.telecom.usuarios.usuario;

public record UsuarioResponse(Long id, String nombre, String email, Rol rol, boolean activo) {
}
