package com.telecom.catalogo.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SecurityConfig {

    // Ver el catálogo es público: no se lee la cookie, así una sesión vencida no lo bloquea
    @Bean
    @Order(1)
    SecurityFilterChain catalogoPublico(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/planes/**"))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    // Crear, editar y dar de baja planes: solo el administrador
    @Bean
    @Order(2)
    SecurityFilterChain gestionDePlanes(HttpSecurity http) throws Exception {
        http
                // La cookie es SameSite=Strict: el navegador no la envía desde otros sitios
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/planes/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(servidor -> servidor
                        .bearerTokenResolver(new CookieTokenResolver())
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(rolesDesdeElToken())));
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${jwt.secreto}") String secreto) {
        SecretKey clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(clave).build();
    }

    // Convierte el claim "rol" del token (ej. ADMINISTRADOR) en ROLE_ADMINISTRADOR
    private JwtAuthenticationConverter rolesDesdeElToken() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
}
