package com.telecom.usuarios.auth.controller;

import com.telecom.usuarios.auth.dto.LoginRequest;
import com.telecom.usuarios.auth.dto.LoginResponse;
import com.telecom.usuarios.auth.dto.RegistroRequest;
import com.telecom.usuarios.auth.mapper.AuthMapper;
import com.telecom.usuarios.auth.service.AuthService;
import com.telecom.usuarios.auth.service.TokenService;
import com.telecom.usuarios.config.CookieDeSesion;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;
    private final CookieDeSesion cookieDeSesion;
    private final AuthMapper authMapper;

    public AuthController(
            AuthService authService,
            UsuarioService usuarioService,
            TokenService tokenService,
            CookieDeSesion cookieDeSesion,
            AuthMapper authMapper) {
        this.authService = authService;
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
        this.cookieDeSesion = cookieDeSesion;
        this.authMapper = authMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = authService.autenticar(request.email(), request.contrasena());
        return conSesionIniciada(usuario, HttpStatus.OK);
    }

    // El cliente queda con la sesión iniciada para seguir directo a contratar un plan
    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(@Valid @RequestBody RegistroRequest request) {
        Usuario cliente = usuarioService.registrarCliente(request.nombre(), request.email(), request.contrasena());
        return conSesionIniciada(cliente, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieDeSesion.borrar().toString())
                .build();
    }

    private ResponseEntity<LoginResponse> conSesionIniciada(Usuario usuario, HttpStatus estado) {
        String token = tokenService.generar(usuario);
        ResponseCookie cookie = cookieDeSesion.crear(token, tokenService.getDuracion());
        return ResponseEntity.status(estado)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authMapper.aLoginResponse(usuario));
    }
}
