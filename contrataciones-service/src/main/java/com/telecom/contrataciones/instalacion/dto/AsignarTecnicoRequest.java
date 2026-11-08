package com.telecom.contrataciones.instalacion.dto;

import jakarta.validation.constraints.NotNull;

public record AsignarTecnicoRequest(@NotNull Long tecnicoId) {
}
