package com.telecom.seguridad;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class CookieTokenResolverTest {

    private final CookieTokenResolver resolver = new CookieTokenResolver();

    @Test
    void leeElTokenDeLaCookieDeSesion() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(SeguridadJwt.NOMBRE_COOKIE, "abc.def.ghi"));

        assertThat(resolver.resolve(request)).isEqualTo("abc.def.ghi");
    }

    @Test
    void sinCookieNoHayToken() {
        assertThat(resolver.resolve(new MockHttpServletRequest())).isNull();
    }

    @Test
    void unaCookieVaciaNoEsUnToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(SeguridadJwt.NOMBRE_COOKIE, " "));

        assertThat(resolver.resolve(request)).isNull();
    }
}
