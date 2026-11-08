package com.telecom.seguridad;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

// Piezas comunes para validar el JWT de la cookie de sesión en todos los microservicios
public final class SeguridadJwt {

    public static final String NOMBRE_COOKIE = "token";

    private SeguridadJwt() {
        // Solo métodos estáticos
    }

    // El secreto tiene que tener al menos 32 caracteres (requisito de HS256)
    public static SecretKey clave(String secreto) {
        return new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public static JwtDecoder decodificador(String secreto) {
        return NimbusJwtDecoder.withSecretKey(clave(secreto)).build();
    }

    // Uso: http.oauth2ResourceServer(SeguridadJwt.tokenDesdeLaCookie())
    public static Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> tokenDesdeLaCookie() {
        return servidor -> servidor
                .bearerTokenResolver(new CookieTokenResolver())
                .jwt(jwt -> jwt.jwtAuthenticationConverter(rolesDesdeElToken()));
    }

    // Convierte el claim "rol" del token (ej. ADMINISTRADOR) en ROLE_ADMINISTRADOR
    static JwtAuthenticationConverter rolesDesdeElToken() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
}
