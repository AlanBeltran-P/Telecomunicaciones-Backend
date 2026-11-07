package com.telecom.usuarios.auth;

import com.telecom.usuarios.usuario.Usuario;
import com.telecom.usuarios.usuario.UsuarioRepository;
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

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            throw new CredencialesInvalidasException();
        }
        return usuario;
    }
}
