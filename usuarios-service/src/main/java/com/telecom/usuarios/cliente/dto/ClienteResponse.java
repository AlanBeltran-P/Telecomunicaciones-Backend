package com.telecom.usuarios.cliente.dto;

public record ClienteResponse(Long id, String nombre, String email, String dni, String telefono, boolean activo) {
}
