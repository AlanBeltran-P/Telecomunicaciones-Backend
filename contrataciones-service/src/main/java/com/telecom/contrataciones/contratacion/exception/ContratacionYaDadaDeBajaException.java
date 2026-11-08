package com.telecom.contrataciones.contratacion.exception;

public class ContratacionYaDadaDeBajaException extends RuntimeException {

    public ContratacionYaDadaDeBajaException(Long id) {
        super("La contratación " + id + " ya estaba dada de baja");
    }
}
