package com.telecom.usuarios.usuario.controller;

import com.telecom.usuarios.usuario.dto.UsuarioResponse;
import com.telecom.usuarios.usuario.mapper.UsuarioMapper;
import com.telecom.usuarios.usuario.service.UsuarioService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// El empleado elige de esta lista el técnico de cada instalación
@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public TecnicoController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping
    public List<UsuarioResponse> listarActivos() {
        return usuarioMapper.aUsuarioResponses(usuarioService.listarTecnicosActivos());
    }
}
