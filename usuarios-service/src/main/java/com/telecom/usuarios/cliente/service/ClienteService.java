package com.telecom.usuarios.cliente.service;

import com.telecom.usuarios.cliente.dto.CrearClienteRequest;
import com.telecom.usuarios.cliente.dto.EditarClienteRequest;
import com.telecom.usuarios.cliente.exception.ClienteNoEncontradoException;
import com.telecom.usuarios.cliente.exception.DniEnUsoException;
import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.exception.EmailEnUsoException;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Un cliente es un usuario con rol CLIENTE y su perfil (DNI y teléfono)
@Service
public class ClienteService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lo usan el registro web y el alta que hace un empleado
    @Transactional
    public Usuario crear(CrearClienteRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailEnUsoException(request.email());
        }
        if (usuarioRepository.existsByDni(request.dni())) {
            throw new DniEnUsoException(request.dni());
        }
        Usuario cliente = new Usuario(
                request.nombre(),
                request.email(),
                passwordEncoder.encode(request.contrasena()),
                Rol.CLIENTE,
                request.dni(),
                request.telefono());
        return usuarioRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return usuarioRepository.findByRol(Rol.CLIENTE);
        }
        return usuarioRepository.buscarPorTexto(Rol.CLIENTE, texto.trim());
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getRol() == Rol.CLIENTE)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    // Puede conservar su propio email y DNI, pero no usar los de otro usuario
    @Transactional
    public Usuario editar(Long id, EditarClienteRequest request) {
        Usuario cliente = buscarPorId(id);
        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailEnUsoException(request.email());
        }
        if (usuarioRepository.existsByDniAndIdNot(request.dni(), id)) {
            throw new DniEnUsoException(request.dni());
        }
        cliente.actualizarDatos(request.nombre(), request.email(), request.dni(), request.telefono());
        return cliente;
    }
}
