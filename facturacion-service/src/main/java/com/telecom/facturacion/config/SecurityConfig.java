package com.telecom.facturacion.config;

import com.telecom.seguridad.SeguridadJwt;
import com.telecom.seguridad.TokenDeServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain facturacion(HttpSecurity http) throws Exception {
        http
                // La cookie es SameSite=Strict: el navegador no la envía desde otros sitios
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/facturas/mias").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/facturas/*/pagos").hasRole("CLIENTE")
                        .requestMatchers("/api/facturas/debito-automatico").hasRole("CLIENTE")
                        .requestMatchers("/api/notificaciones/**").hasRole("CLIENTE")
                        // Solo otros microservicios; el Gateway no expone estas rutas
                        .requestMatchers("/api/internos/**").hasRole(TokenDeServicio.ROL)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(SeguridadJwt.tokenDesdeLaCookie());
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${jwt.secreto}") String secreto) {
        return SeguridadJwt.decodificador(secreto);
    }
}
