package com.telecom.facturacion.notificacion.client;

public interface EnviadorDeEmail {

    void enviar(String destinatario, String asunto, String mensaje);
}
