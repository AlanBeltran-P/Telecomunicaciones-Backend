package com.telecom.usuarios.config;

import com.telecom.seguridad.SeguridadJwt;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieDeSesion {

    public static final String NOMBRE = SeguridadJwt.NOMBRE_COOKIE;

    // En local (http) va en false; en producción (https) siempre true
    private final boolean segura;

    public CookieDeSesion(@Value("${sesion.cookie-segura}") boolean segura) {
        this.segura = segura;
    }

    public ResponseCookie crear(String token, Duration duracion) {
        return base(token).maxAge(duracion).build();
    }

    public ResponseCookie borrar() {
        return base("").maxAge(0).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String valor) {
        return ResponseCookie.from(NOMBRE, valor)
                .httpOnly(true)
                .secure(segura)
                .sameSite("Strict")
                .path("/");
    }
}
