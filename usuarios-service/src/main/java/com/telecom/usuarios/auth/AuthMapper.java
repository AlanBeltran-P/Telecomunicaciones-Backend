package com.telecom.usuarios.auth;

import com.telecom.usuarios.usuario.Usuario;
import org.mapstruct.Mapper;

@Mapper
public interface AuthMapper {

    LoginResponse aLoginResponse(Usuario usuario);
}
