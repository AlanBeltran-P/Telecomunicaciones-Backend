package com.telecom.usuarios.usuario;

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
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailEnUsoException(request.email());
        }
        String contrasenaEncriptada = passwordEncoder.encode(request.contrasena());
        Usuario usuario = new Usuario(request.nombre(), request.email(), contrasenaEncriptada, request.rol());
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
