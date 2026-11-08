package com.telecom.contrataciones.instalacion.controller;

import com.telecom.contrataciones.instalacion.dto.AsignarTecnicoRequest;
import com.telecom.contrataciones.instalacion.dto.InstalacionResponse;
import com.telecom.contrataciones.instalacion.mapper.InstalacionMapper;
import com.telecom.contrataciones.instalacion.service.InstalacionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instalaciones")
public class InstalacionController {

    private final InstalacionService instalacionService;
    private final InstalacionMapper instalacionMapper;

    public InstalacionController(InstalacionService instalacionService, InstalacionMapper instalacionMapper) {
        this.instalacionService = instalacionService;
        this.instalacionMapper = instalacionMapper;
    }

    @GetMapping("/pendientes")
    public List<InstalacionResponse> listarPendientes() {
        return instalacionMapper.aInstalacionResponses(instalacionService.listarPendientes());
    }

    @PutMapping("/{contratacionId}/tecnico")
    public InstalacionResponse asignarTecnico(
            @PathVariable Long contratacionId,
            @Valid @RequestBody AsignarTecnicoRequest request) {
        return instalacionMapper.aInstalacionResponse(
                instalacionService.asignarTecnico(contratacionId, request.tecnicoId()));
    }
}
