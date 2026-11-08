package com.telecom.usuarios.auth.mapper;

import com.telecom.usuarios.auth.dto.LoginResponse;
import com.telecom.usuarios.usuario.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper
public interface AuthMapper {

    LoginResponse aLoginResponse(Usuario usuario);
}
