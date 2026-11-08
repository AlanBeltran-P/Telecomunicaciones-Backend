package com.telecom.errores;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

// Se activa sola en cada microservicio que dependa de este módulo (ver META-INF/spring/...imports)
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import(ManejoDeErroresDeNegocio.class)
public class ErroresAutoConfiguration {
}
