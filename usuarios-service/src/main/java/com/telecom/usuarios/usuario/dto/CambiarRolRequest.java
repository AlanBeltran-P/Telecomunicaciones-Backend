package com.telecom.usuarios.usuario.dto;

import com.telecom.usuarios.usuario.entity.Rol;
import jakarta.validation.constraints.NotNull;

public record CambiarRolRequest(@NotNull Rol rol) {
}
