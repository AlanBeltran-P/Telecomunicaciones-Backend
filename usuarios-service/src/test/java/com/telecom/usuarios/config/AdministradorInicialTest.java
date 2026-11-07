package com.telecom.usuarios.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.telecom.usuarios.usuario.Rol;
import com.telecom.usuarios.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdministradorInicialTest {

    @Autowired
    private AdministradorInicial administradorInicial;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void alArrancarCreaElAdministrador() {
        assertThat(usuarioRepository.findByEmail("admin@telecom.com"))
                .hasValueSatisfying(usuario -> assertThat(usuario.getRol()).isEqualTo(Rol.ADMINISTRADOR));
    }

    @Test
    void siYaExisteNoLoDuplica() {
        administradorInicial.run(new DefaultApplicationArguments());

        assertThat(usuarioRepository.count()).isEqualTo(1);
    }
}
