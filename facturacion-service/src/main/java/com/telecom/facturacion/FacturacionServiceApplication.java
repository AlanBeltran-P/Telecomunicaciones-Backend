package com.telecom.facturacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling: activa los procesos automáticos (por ejemplo, la facturación diaria)
@SpringBootApplication
@EnableScheduling
public class FacturacionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacturacionServiceApplication.class, args);
    }
}
