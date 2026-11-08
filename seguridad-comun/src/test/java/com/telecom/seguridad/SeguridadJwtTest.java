package com.telecom.seguridad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class SeguridadJwtTest {

    private static final String SECRETO = "secreto-de-prueba-de-al-menos-32-caracteres";

    @Test
    void elDecodificadorAceptaUnTokenFirmadoConElMismoSecreto() {
        Jwt jwt = SeguridadJwt.decodificador(SECRETO).decode(token("ADMINISTRADOR", SECRETO));

        assertThat(jwt.getClaimAsString("rol")).isEqualTo("ADMINISTRADOR");
    }

    @Test
    void elDecodificadorRechazaUnTokenFirmadoConOtroSecreto() {
        String token = token("ADMINISTRADOR", "otro-secreto-de-al-menos-32-caracteres!!");

        assertThatThrownBy(() -> SeguridadJwt.decodificador(SECRETO).decode(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void elRolDelTokenSeConvierteEnUnRolDeSpring() {
        Jwt jwt = SeguridadJwt.decodificador(SECRETO).decode(token("TECNICO", SECRETO));

        var autenticacion = SeguridadJwt.rolesDesdeElToken().convert(jwt);

        assertThat(autenticacion.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_TECNICO");
    }

    private String token(String rol, String secreto) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("usuario@telecom.com")
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(Duration.ofHours(1)))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return new NimbusJwtEncoder(new ImmutableSecret<>(SeguridadJwt.clave(secreto)))
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }
}
