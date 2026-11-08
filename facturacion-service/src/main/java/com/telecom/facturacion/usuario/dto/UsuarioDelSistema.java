package com.telecom.facturacion.usuario.dto;

// Lo que devuelve usuarios-service en /api/internos/usuarios/{id}
public record UsuarioDelSistema(Long id, String nombre, String email, String rol, boolean activo) {
}
