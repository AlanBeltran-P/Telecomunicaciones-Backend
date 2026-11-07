package com.telecom.usuarios.auth;

import com.telecom.usuarios.usuario.Usuario;
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

    public AuthController(AuthService authService, TokenService tokenService, CookieDeSesion cookieDeSesion) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.cookieDeSesion = cookieDeSesion;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = authService.autenticar(request.email(), request.contrasena());
        String token = tokenService.generar(usuario);
        ResponseCookie cookie = cookieDeSesion.crear(token, tokenService.getDuracion());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(usuario.getEmail(), usuario.getRol()));
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
