package com.telecom.catalogo.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CookieDeSesionTest {

    private static final String SECRETO = "secreto-de-prueba-de-al-menos-32-caracteres";

    private static final String PLAN_JSON = """
            {"nombre": "Internet 300 MB", "precio": 15000}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void aceptaLaCookieDeUnAdministrador() throws Exception {
        Cookie cookie = cookieCon(token("ADMINISTRADOR", SECRETO));

        mockMvc.perform(crearPlanCon(cookie))
                .andExpect(status().isCreated());
    }

    @Test
    void rechazaUnTokenFirmadoConOtroSecreto() throws Exception {
        Cookie cookie = cookieCon(token("ADMINISTRADOR", "otro-secreto-de-al-menos-32-caracteres!!"));

        mockMvc.perform(crearPlanCon(cookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaElTokenDeUnRolSinPermiso() throws Exception {
        Cookie cookie = cookieCon(token("CLIENTE", SECRETO));

        mockMvc.perform(crearPlanCon(cookie))
                .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder crearPlanCon(Cookie cookie) {
        return post("/api/planes")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(PLAN_JSON);
    }

    private Cookie cookieCon(String token) {
        return new Cookie(CookieTokenResolver.NOMBRE_COOKIE, token);
    }

    private String token(String rol, String secreto) {
        SecretKey clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("usuario@telecom.com")
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(Duration.ofHours(1)))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return new NimbusJwtEncoder(new ImmutableSecret<>(clave))
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }
}
