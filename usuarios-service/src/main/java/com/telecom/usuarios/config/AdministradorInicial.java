package com.telecom.usuarios.config;

import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import com.telecom.usuarios.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Crea el primer administrador al arrancar, si todavía no existe
@Component
public class AdministradorInicial implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String contrasena;

    public AdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.email}") String email,
            @Value("${admin.contrasena}") String contrasena) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.contrasena = contrasena;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }
        Usuario administrador = new Usuario(
                "Administrador", email, passwordEncoder.encode(contrasena), Rol.ADMINISTRADOR);
        usuarioRepository.save(administrador);
    }
}
