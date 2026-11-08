package com.telecom.usuarios.auth.controller;

import com.telecom.usuarios.auth.dto.LoginRequest;
import com.telecom.usuarios.auth.dto.LoginResponse;
import com.telecom.usuarios.auth.exception.CredencialesInvalidasException;
import com.telecom.usuarios.auth.mapper.AuthMapper;
import com.telecom.usuarios.auth.service.AuthService;
import com.telecom.usuarios.auth.service.TokenService;
import com.telecom.usuarios.config.CookieDeSesion;
import com.telecom.usuarios.usuario.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final CookieDeSesion cookieDeSesion;
    private final AuthMapper authMapper;

    public AuthController(
            AuthService authService,
            TokenService tokenService,
            CookieDeSesion cookieDeSesion,
            AuthMapper authMapper) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.cookieDeSesion = cookieDeSesion;
        this.authMapper = authMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = authService.autenticar(request.email(), request.contrasena());
        String token = tokenService.generar(usuario);
        ResponseCookie cookie = cookieDeSesion.crear(token, tokenService.getDuracion());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authMapper.aLoginResponse(usuario));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieDeSesion.borrar().toString())
                .build();
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void credencialesInvalidas() {
        // Responde 401 sin indicar si falló el email o la contraseña
    }
}
