package com.telecom.usuarios.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.telecom.usuarios.auth.dto.LoginResponse;
import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AuthMapperTest {

    private final AuthMapper authMapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void laRespuestaDelLoginLlevaElEmailYElRol() {
        Usuario usuario = new Usuario("Administrador", "admin@telecom.com", "hash", Rol.ADMINISTRADOR);

        LoginResponse respuesta = authMapper.aLoginResponse(usuario);

        assertThat(respuesta.email()).isEqualTo("admin@telecom.com");
        assertThat(respuesta.rol()).isEqualTo(Rol.ADMINISTRADOR);
    }
}
