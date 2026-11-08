package com.telecom.usuarios.auth.service;

import com.telecom.usuarios.auth.exception.CredencialesInvalidasException;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario autenticar(String email, String contrasena) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(CredencialesInvalidasException::new);

        boolean contrasenaCorrecta = passwordEncoder.matches(contrasena, usuario.getContrasena());
        if (!usuario.isActivo() || !contrasenaCorrecta) {
            throw new CredencialesInvalidasException();
        }
        return usuario;
    }
}
