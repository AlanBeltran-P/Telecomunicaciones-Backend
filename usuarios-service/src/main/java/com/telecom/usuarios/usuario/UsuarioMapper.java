package com.telecom.usuarios.usuario;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface UsuarioMapper {

    UsuarioResponse aUsuarioResponse(Usuario usuario);

    List<UsuarioResponse> aUsuarioResponses(List<Usuario> usuarios);
}
