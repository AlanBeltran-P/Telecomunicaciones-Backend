package com.telecom.usuarios.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class TokenServiceTest {

    private final SecretKey clave = new SecretKeySpec(
            "secreto-de-prueba-de-al-menos-32-caracteres".getBytes(StandardCharsets.UTF_8), "HmacSHA256");

    private final TokenService tokenService = new TokenService(
            new NimbusJwtEncoder(new ImmutableSecret<>(clave)), Duration.ofHours(1));

    @Test
    void elTokenLlevaElEmailYElRol() {
        Usuario usuario = administradorConId(7L);

        Jwt jwt = decodificar(tokenService.generar(usuario));

        assertThat(jwt.getSubject()).isEqualTo("admin@telecom.com");
        assertThat(jwt.getClaimAsString("rol")).isEqualTo("ADMINISTRADOR");
        assertThat(jwt.getClaim("usuarioId").toString()).isEqualTo("7");
    }

    @Test
    void elTokenVenceSegunLaDuracionConfigurada() {
        Usuario usuario = administradorConId(7L);

        Jwt jwt = decodificar(tokenService.generar(usuario));

        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(Duration.ofHours(1));
    }

    // El id lo asigna la base; en un test unitario se fija a mano
    private Usuario administradorConId(Long id) {
        Usuario usuario = new Usuario("Administrador", "admin@telecom.com", "hash", Rol.ADMINISTRADOR);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private Jwt decodificar(String token) {
        return NimbusJwtDecoder.withSecretKey(clave).build().decode(token);
    }
}
