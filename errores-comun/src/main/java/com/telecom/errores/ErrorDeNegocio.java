package com.telecom.errores;

import java.util.Arrays;
import org.springframework.http.HttpStatus;

// Error que se le muestra al usuario. El texto no está acá: 'codigo' es la clave en messages.properties
// (español) y messages_en.properties (inglés), y se traduce según el idioma del pedido.
public abstract class ErrorDeNegocio extends RuntimeException {

    private final HttpStatus estado;
    private final String codigo;
    private final Object[] argumentos;

    protected ErrorDeNegocio(HttpStatus estado, String codigo, Object... argumentos) {
        this(estado, codigo, null, argumentos);
    }

    protected ErrorDeNegocio(HttpStatus estado, String codigo, Throwable causa, Object... argumentos) {
        super(codigo + " " + Arrays.toString(argumentos), causa);
        this.estado = estado;
        this.codigo = codigo;
        // Como texto: así un id 1000 no se muestra como "1.000" ni "1,000"
        this.argumentos = Arrays.stream(argumentos).map(String::valueOf).toArray();
    }

    public HttpStatus getEstado() {
        return estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public Object[] getArgumentos() {
        return argumentos.clone();
    }
}
