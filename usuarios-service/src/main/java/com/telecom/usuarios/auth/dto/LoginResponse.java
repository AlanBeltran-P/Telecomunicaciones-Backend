package com.telecom.usuarios.auth.dto;

import com.telecom.usuarios.usuario.entity.Rol;

public record LoginResponse(Long id, String email, Rol rol) {
}
