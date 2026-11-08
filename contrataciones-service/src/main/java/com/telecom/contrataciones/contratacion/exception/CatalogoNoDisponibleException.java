package com.telecom.contrataciones.contratacion.exception;

public class CatalogoNoDisponibleException extends RuntimeException {

    public CatalogoNoDisponibleException(Throwable causa) {
        super("No se pudo consultar el catálogo de planes. Probá de nuevo en unos minutos", causa);
    }
}
