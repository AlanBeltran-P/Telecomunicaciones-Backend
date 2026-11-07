package com.telecom.catalogo.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.web.util.WebUtils;

// Lee el JWT de la cookie de sesión en lugar del header Authorization
public class CookieTokenResolver implements BearerTokenResolver {

    public static final String NOMBRE_COOKIE = "token";

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, NOMBRE_COOKIE);
        if (cookie == null || cookie.getValue().isBlank()) {
            return null;
        }
        return cookie.getValue();
    }
}
