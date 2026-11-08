package com.telecom.seguridad;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class TokenDeServicioTest {

    private static final String SECRETO = "secreto-de-prueba-de-al-menos-32-caracteres";

    @Test
    void elTokenLlevaElNombreDelServicioYElRolSistema() {
        Jwt jwt = SeguridadJwt.decodificador(SECRETO).decode(TokenDeServicio.generar(SECRETO, "facturacion-service"));

        assertThat(jwt.getSubject()).isEqualTo("facturacion-service");
        assertThat(SeguridadJwt.rolesDesdeElToken().convert(jwt).getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_SISTEMA");
    }

    @Test
    void elTokenDuraCincoMinutos() {
        Jwt jwt = SeguridadJwt.decodificador(SECRETO).decode(TokenDeServicio.generar(SECRETO, "facturacion-service"));

        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(Duration.ofMinutes(5));
    }

    @Test
    void comoCookieUsaElNombreDeLaCookieDeSesion() {
        assertThat(TokenDeServicio.comoCookie(SECRETO, "facturacion-service")).startsWith("token=");
    }
}
