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

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanResponse crear(@Valid @RequestBody PlanRequest request) {
        return PlanResponse.desde(planService.crear(request));
    }

    @GetMapping
    public List<PlanResponse> listar() {
        return planService.listarActivos().stream()
                .map(PlanResponse::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public PlanResponse buscar(@PathVariable Long id) {
        return PlanResponse.desde(planService.buscarActivo(id));
    }

    @PutMapping("/{id}")
    public PlanResponse actualizar(@PathVariable Long id, @Valid @RequestBody PlanRequest request) {
        return PlanResponse.desde(planService.actualizar(id, request));
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
