package com.telecom.usuarios.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.telecom.usuarios.usuario.Rol;
import com.telecom.usuarios.usuario.Usuario;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AuthMapperTest {

    private final AuthMapper authMapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void laRespuestaDelLoginLlevaElEmailYElRol() {
        Usuario usuario = new Usuario("admin@telecom.com", "hash", Rol.ADMINISTRADOR);

        LoginResponse respuesta = authMapper.aLoginResponse(usuario);

        assertThat(respuesta.email()).isEqualTo("admin@telecom.com");
        assertThat(respuesta.rol()).isEqualTo(Rol.ADMINISTRADOR);
    }
}
