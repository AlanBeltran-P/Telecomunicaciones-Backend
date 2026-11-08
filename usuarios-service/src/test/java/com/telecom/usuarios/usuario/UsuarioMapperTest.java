package com.telecom.usuarios.usuario;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UsuarioMapperTest {

    private final UsuarioMapper usuarioMapper = Mappers.getMapper(UsuarioMapper.class);

    @Test
    void laRespuestaLlevaLosDatosDelUsuarioSinLaContrasena() {
        Usuario usuario = new Usuario("Ana Pérez", "ana@telecom.com", "hash", Rol.TECNICO);
        usuario.desactivar();

        UsuarioResponse respuesta = usuarioMapper.aUsuarioResponse(usuario);

        assertThat(respuesta.nombre()).isEqualTo("Ana Pérez");
        assertThat(respuesta.email()).isEqualTo("ana@telecom.com");
        assertThat(respuesta.rol()).isEqualTo(Rol.TECNICO);
        assertThat(respuesta.activo()).isFalse();
    }
}
