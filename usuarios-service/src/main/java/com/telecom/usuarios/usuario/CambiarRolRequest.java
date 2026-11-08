package com.telecom.usuarios.usuario;

import jakarta.validation.constraints.NotNull;

public record CambiarRolRequest(@NotNull Rol rol) {
}
