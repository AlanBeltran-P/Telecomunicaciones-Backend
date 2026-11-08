package com.telecom.usuarios.cliente.mapper;

import com.telecom.usuarios.cliente.dto.ClienteResponse;
import com.telecom.usuarios.usuario.entity.Usuario;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface ClienteMapper {

    ClienteResponse aClienteResponse(Usuario cliente);

    List<ClienteResponse> aClienteResponses(List<Usuario> clientes);
}
