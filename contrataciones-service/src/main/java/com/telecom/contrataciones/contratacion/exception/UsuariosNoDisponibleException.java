package com.telecom.contrataciones.contratacion.exception;

public class UsuariosNoDisponibleException extends RuntimeException {

    public UsuariosNoDisponibleException(Throwable causa) {
        super("No se pudo consultar los datos del cliente. Probá de nuevo en unos minutos", causa);
    }
}
