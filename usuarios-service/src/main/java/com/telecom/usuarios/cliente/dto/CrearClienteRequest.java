package com.telecom.usuarios.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CrearClienteRequest(
        @NotBlank String nombre,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String contrasena,
        @NotBlank @Pattern(regexp = "\\d{7,8}", message = "debe tener 7 u 8 dígitos") String dni,
        @NotBlank @Pattern(regexp = "[0-9+()\\- ]{6,20}", message = "no es un teléfono válido") String telefono) {
}
