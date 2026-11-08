package com.telecom.usuarios.usuario.controller;

import com.telecom.usuarios.usuario.dto.CambiarRolRequest;
import com.telecom.usuarios.usuario.dto.CrearUsuarioRequest;
import com.telecom.usuarios.usuario.dto.UsuarioResponse;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.mapper.UsuarioMapper;
import com.telecom.usuarios.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest request) {
        return usuarioMapper.aUsuarioResponse(usuarioService.crear(request));
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioMapper.aUsuarioResponses(usuarioService.listarPersonal());
    }

    @PutMapping("/{id}/rol")
    public UsuarioResponse cambiarRol(
            @PathVariable Long id,
            @Valid @RequestBody CambiarRolRequest request,
            @AuthenticationPrincipal Jwt administrador) {
        Usuario usuario = usuarioService.cambiarRol(id, request.rol(), administrador.getSubject());
        return usuarioMapper.aUsuarioResponse(usuario);
    }

    @PutMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id, @AuthenticationPrincipal Jwt administrador) {
        usuarioService.desactivar(id, administrador.getSubject());
    }
}
