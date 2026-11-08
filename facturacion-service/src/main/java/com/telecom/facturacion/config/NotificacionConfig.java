package com.telecom.facturacion.config;

import com.telecom.facturacion.notificacion.client.EmailEnLog;
import com.telecom.facturacion.notificacion.client.EmailPorSmtp;
import com.telecom.facturacion.notificacion.client.EnviadorDeEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

// Con spring.mail.host configurado los emails salen por SMTP; si no, se escriben en el log
@Configuration
public class NotificacionConfig {

    @Bean
    @ConditionalOnProperty("spring.mail.host")
    EnviadorDeEmail emailPorSmtp(JavaMailSender mailSender, @Value("${notificaciones.remitente}") String remitente) {
        return new EmailPorSmtp(mailSender, remitente);
    }

    @Bean
    @ConditionalOnMissingBean(EnviadorDeEmail.class)
    EnviadorDeEmail emailEnLog() {
        return new EmailEnLog();
    }
}
