package com.telecom.facturacion.notificacion.client;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

// Se usa cuando está configurado spring.mail.host (ver NotificacionConfig)
public class EmailPorSmtp implements EnviadorDeEmail {

    private final JavaMailSender mailSender;
    private final String remitente;

    public EmailPorSmtp(JavaMailSender mailSender, String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Override
    public void enviar(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(remitente);
        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(mensaje);
        mailSender.send(email);
    }
}
