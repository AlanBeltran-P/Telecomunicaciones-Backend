package com.telecom.usuarios.cliente.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.telecom.usuarios.cliente.dto.ClienteResponse;
import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ClienteMapperTest {

    private final ClienteMapper clienteMapper = Mappers.getMapper(ClienteMapper.class);

    @Test
    void laRespuestaLlevaElPerfilDelClienteSinLaContrasena() {
        Usuario cliente = new Usuario("María López", "maria@mail.com", "hash", Rol.CLIENTE, "30111222", "11 4444-5555");

        ClienteResponse respuesta = clienteMapper.aClienteResponse(cliente);

        assertThat(respuesta.nombre()).isEqualTo("María López");
        assertThat(respuesta.dni()).isEqualTo("30111222");
        assertThat(respuesta.telefono()).isEqualTo("11 4444-5555");
        assertThat(respuesta.activo()).isTrue();
    }
}
