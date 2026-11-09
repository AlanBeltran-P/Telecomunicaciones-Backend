package com.telecom.contrataciones.config;

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
    SecurityFilterChain contrataciones(HttpSecurity http) throws Exception {
        http
                // La cookie es SameSite=Strict: el navegador no la envía desde otros sitios
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contrataciones").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/contrataciones/mias").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/contrataciones/reporte").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/contrataciones/clientes/**").hasRole("EMPLEADO")
                        .requestMatchers(HttpMethod.PUT, "/api/contrataciones/*/baja").hasRole("EMPLEADO")
                        .requestMatchers("/api/contrataciones/suspendidas").hasRole("EMPLEADO")
                        .requestMatchers(HttpMethod.PUT, "/api/contrataciones/*/reactivacion").hasRole("EMPLEADO")
                        .requestMatchers("/api/instalaciones/pendientes").hasRole("EMPLEADO")
                        .requestMatchers(HttpMethod.PUT, "/api/instalaciones/*/tecnico").hasRole("EMPLEADO")
                        .requestMatchers("/api/instalaciones/mias").hasRole("TECNICO")
                        // Solo otros microservicios; el Gateway no expone estas rutas
                        .requestMatchers("/api/internos/**").hasRole(TokenDeServicio.ROL)
                        .requestMatchers(HttpMethod.PUT, "/api/instalaciones/*/realizada").hasRole("TECNICO")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(SeguridadJwt.tokenDesdeLaCookie());
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${jwt.secreto}") String secreto) {
        return SeguridadJwt.decodificador(secreto);
    }
}
