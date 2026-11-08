package com.telecom.errores;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// Aplicación mínima para probar el módulo como lo usaría un microservicio
@SpringBootApplication
class AplicacionDePrueba {

    static class PlanNoEncontradoException extends ErrorDeNegocio {
        PlanNoEncontradoException(Long id) {
            super(HttpStatus.NOT_FOUND, "plan.no-encontrado", id);
        }
    }

    record PedidoDePrueba(@NotBlank(message = "{campo.obligatorio}") String nombre) {
    }

    @RestController
    static class ControllerDePrueba {

        @GetMapping("/planes/{id}")
        String buscar(@PathVariable Long id) {
            throw new PlanNoEncontradoException(id);
        }

        @PostMapping("/planes")
        String crear(@Valid @RequestBody PedidoDePrueba pedido) {
            return pedido.nombre();
        }
    }
}
