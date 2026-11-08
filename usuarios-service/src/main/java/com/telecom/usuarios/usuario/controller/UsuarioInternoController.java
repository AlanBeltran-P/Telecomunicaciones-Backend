package com.telecom.usuarios.usuario.controller;

import com.telecom.usuarios.usuario.dto.UsuarioInternoResponse;
import com.telecom.usuarios.usuario.mapper.UsuarioMapper;
import com.telecom.usuarios.usuario.service.UsuarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Consultas para otros microservicios (rol SISTEMA); el Gateway no expone estas rutas
@RestController
@RequestMapping("/api/internos/usuarios")
public class UsuarioInternoController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioInternoController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping("/{id}")
    public UsuarioInternoResponse buscar(@PathVariable Long id) {
        return usuarioMapper.aUsuarioInternoResponse(usuarioService.buscarPorId(id));
    }
}
