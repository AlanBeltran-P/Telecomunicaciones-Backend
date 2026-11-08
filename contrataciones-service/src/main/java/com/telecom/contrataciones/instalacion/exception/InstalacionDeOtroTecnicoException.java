package com.telecom.contrataciones.instalacion.exception;

public class InstalacionDeOtroTecnicoException extends RuntimeException {

    public InstalacionDeOtroTecnicoException(Long contratacionId) {
        super("La instalación de la contratación " + contratacionId + " no está asignada a vos");
    }
}
