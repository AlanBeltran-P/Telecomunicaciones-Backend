package com.telecom.seguridad;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

// Token para que un microservicio llame a otro sin un usuario de por medio
// (por ejemplo, un proceso programado como la facturación mensual)
public final class TokenDeServicio {

    public static final String ROL = "SISTEMA";

    private static final Duration DURACION = Duration.ofMinutes(5);

    private TokenDeServicio() {
        // Solo métodos estáticos
    }

    public static String generar(String secreto, String nombreDelServicio) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(nombreDelServicio)
                .claim("rol", ROL)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(DURACION))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return new NimbusJwtEncoder(new ImmutableSecret<>(SeguridadJwt.clave(secreto)))
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    // Valor listo para el header Cookie: así el otro servicio lo valida igual que la sesión de un usuario
    public static String comoCookie(String secreto, String nombreDelServicio) {
        return SeguridadJwt.NOMBRE_COOKIE + "=" + generar(secreto, nombreDelServicio);
    }
}
