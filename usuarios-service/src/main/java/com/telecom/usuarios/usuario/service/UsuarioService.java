package com.telecom.usuarios.usuario.service;

import com.telecom.usuarios.usuario.dto.CrearUsuarioRequest;
import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.exception.EmailEnUsoException;
import com.telecom.usuarios.usuario.exception.OperacionNoPermitidaException;
import com.telecom.usuarios.usuario.exception.UsuarioNoEncontradoException;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    // Los clientes se registran solos: no se crean ni se gestionan desde acá
    private static final Set<Rol> ROLES_DEL_PERSONAL = EnumSet.of(Rol.ADMINISTRADOR, Rol.EMPLEADO, Rol.TECNICO);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario crear(CrearUsuarioRequest request) {
        validarRolDelPersonal(request.rol());
        return guardarNuevo(request.nombre(), request.email(), request.contrasena(), request.rol());
    }

    // Registro desde la web: siempre crea un cliente
    @Transactional
    public Usuario registrarCliente(String nombre, String email, String contrasena) {
        return guardarNuevo(nombre, email, contrasena, Rol.CLIENTE);
    }

    private Usuario guardarNuevo(String nombre, String email, String contrasena, Rol rol) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailEnUsoException(email);
        }
        Usuario usuario = new Usuario(nombre, email, passwordEncoder.encode(contrasena), rol);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPersonal() {
        return usuarioRepository.findByRolNot(Rol.CLIENTE);
    }

    @Transactional
    public Usuario cambiarRol(Long id, Rol nuevoRol, String emailDelAdministrador) {
        validarRolDelPersonal(nuevoRol);
        Usuario usuario = buscarPersonal(id);
        validarQueNoEsElMismoAdministrador(usuario, emailDelAdministrador);
        usuario.cambiarRol(nuevoRol);
        return usuario;
    }

    @Transactional
    public void desactivar(Long id, String emailDelAdministrador) {
        Usuario usuario = buscarPersonal(id);
        validarQueNoEsElMismoAdministrador(usuario, emailDelAdministrador);
        usuario.desactivar();
    }

    private Usuario buscarPersonal(Long id) {
        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getRol() != Rol.CLIENTE)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    private void validarRolDelPersonal(Rol rol) {
        if (!ROLES_DEL_PERSONAL.contains(rol)) {
            throw new OperacionNoPermitidaException("El rol " + rol + " no se asigna desde la gestión de usuarios");
        }
    }

    // Evita que el sistema se quede sin administrador por un error
    private void validarQueNoEsElMismoAdministrador(Usuario usuario, String emailDelAdministrador) {
        if (usuario.getEmail().equals(emailDelAdministrador)) {
            throw new OperacionNoPermitidaException("Un administrador no puede cambiar su propio rol ni desactivarse");
        }
    }
}
