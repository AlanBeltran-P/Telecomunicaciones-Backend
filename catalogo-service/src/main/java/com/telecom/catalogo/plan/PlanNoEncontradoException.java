package com.telecom.catalogo.plan;

public class PlanNoEncontradoException extends RuntimeException {

    public PlanNoEncontradoException(Long id) {
        super("No existe un plan activo con id " + id);
    }
}
