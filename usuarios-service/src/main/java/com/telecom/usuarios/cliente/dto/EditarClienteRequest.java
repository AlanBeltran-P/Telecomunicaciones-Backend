package com.telecom.usuarios.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EditarClienteRequest(
        @NotBlank String nombre,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{7,8}", message = "debe tener 7 u 8 dígitos") String dni,
        @NotBlank @Pattern(regexp = "[0-9+()\\- ]{6,20}", message = "no es un teléfono válido") String telefono) {
}
