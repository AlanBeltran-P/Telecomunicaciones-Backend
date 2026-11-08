package com.telecom.facturacion.notificacion.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Para desarrollo: sin servidor de correo configurado, el email se escribe en el log
public class EmailEnLog implements EnviadorDeEmail {

    private static final Logger log = LoggerFactory.getLogger(EmailEnLog.class);

    @Override
    public void enviar(String destinatario, String asunto, String mensaje) {
        log.info("Email (no enviado, falta spring.mail.host) para {}: {} - {}", destinatario, asunto, mensaje);
    }
}
