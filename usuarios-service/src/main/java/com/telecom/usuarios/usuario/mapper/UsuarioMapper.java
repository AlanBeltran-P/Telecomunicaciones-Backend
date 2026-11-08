package com.telecom.usuarios.usuario.mapper;

import com.telecom.usuarios.usuario.dto.PerfilResponse;
import com.telecom.usuarios.usuario.dto.UsuarioInternoResponse;
import com.telecom.usuarios.usuario.dto.UsuarioResponse;
import com.telecom.usuarios.usuario.entity.Usuario;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface UsuarioMapper {

    UsuarioResponse aUsuarioResponse(Usuario usuario);

    List<UsuarioResponse> aUsuarioResponses(List<Usuario> usuarios);

    UsuarioInternoResponse aUsuarioInternoResponse(Usuario usuario);

    PerfilResponse aPerfilResponse(Usuario usuario);
}
