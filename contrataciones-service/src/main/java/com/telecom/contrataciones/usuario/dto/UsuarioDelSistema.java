package com.telecom.contrataciones.usuario.dto;

// Lo que devuelve usuarios-service en /api/internos/usuarios/{id}
public record UsuarioDelSistema(Long id, String nombre, String email, String rol, boolean activo) {

    public boolean esClienteActivo() {
        return activo && "CLIENTE".equals(rol);
    }
}
