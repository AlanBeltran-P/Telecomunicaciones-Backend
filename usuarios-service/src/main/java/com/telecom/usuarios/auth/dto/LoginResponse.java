package com.telecom.usuarios.auth.dto;

import com.telecom.usuarios.usuario.entity.Rol;

public record LoginResponse(String email, Rol rol) {
}
