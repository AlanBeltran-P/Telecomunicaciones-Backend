package com.telecom.usuarios.usuario.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(Long id) {
        super("No existe un usuario del personal con id " + id);
    }
}
