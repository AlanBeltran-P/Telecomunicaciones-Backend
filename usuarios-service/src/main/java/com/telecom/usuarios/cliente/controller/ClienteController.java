package com.telecom.usuarios.cliente.controller;

import com.telecom.usuarios.cliente.dto.ClienteResponse;
import com.telecom.usuarios.cliente.dto.CrearClienteRequest;
import com.telecom.usuarios.cliente.dto.EditarClienteRequest;
import com.telecom.usuarios.cliente.mapper.ClienteMapper;
import com.telecom.usuarios.cliente.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    public ClienteController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody CrearClienteRequest request) {
        return clienteMapper.aClienteResponse(clienteService.crear(request));
    }

    @GetMapping
    public List<ClienteResponse> buscar(@RequestParam(required = false) String busqueda) {
        return clienteMapper.aClienteResponses(clienteService.buscar(busqueda));
    }

    @GetMapping("/{id}")
    public ClienteResponse ver(@PathVariable Long id) {
        return clienteMapper.aClienteResponse(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ClienteResponse editar(@PathVariable Long id, @Valid @RequestBody EditarClienteRequest request) {
        return clienteMapper.aClienteResponse(clienteService.editar(id, request));
    }
}
