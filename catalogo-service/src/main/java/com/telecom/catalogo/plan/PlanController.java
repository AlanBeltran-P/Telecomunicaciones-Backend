package com.telecom.catalogo.plan;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/planes")
public class PlanController {

    private final PlanService planService;
    private final PlanMapper planMapper;

    public PlanController(PlanService planService, PlanMapper planMapper) {
        this.planService = planService;
        this.planMapper = planMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanResponse crear(@Valid @RequestBody PlanRequest request) {
        return planMapper.aPlanResponse(planService.crear(request));
    }

    @GetMapping
    public List<PlanResponse> listar() {
        return planMapper.aPlanResponses(planService.listarActivos());
    }

    @GetMapping("/{id}")
    public PlanResponse buscar(@PathVariable Long id) {
        return planMapper.aPlanResponse(planService.buscarActivo(id));
    }

    @PutMapping("/{id}")
    public PlanResponse actualizar(@PathVariable Long id, @Valid @RequestBody PlanRequest request) {
        return planMapper.aPlanResponse(planService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Long id) {
        planService.darDeBaja(id);
    }

    @ExceptionHandler(PlanNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void planNoEncontrado() {
        // Responde 404
    }
}
