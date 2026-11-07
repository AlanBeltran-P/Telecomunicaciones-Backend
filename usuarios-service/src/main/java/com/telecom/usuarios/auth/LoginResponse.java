package com.telecom.usuarios.auth;

import com.telecom.usuarios.usuario.Rol;

public record LoginResponse(String email, Rol rol) {
}
