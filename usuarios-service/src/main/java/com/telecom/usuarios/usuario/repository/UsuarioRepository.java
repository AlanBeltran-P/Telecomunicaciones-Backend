package com.telecom.usuarios.usuario.repository;

import com.telecom.usuarios.usuario.entity.Rol;
import com.telecom.usuarios.usuario.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRolNot(Rol rol);

    List<Usuario> findByRol(Rol rol);

    boolean existsByDni(String dni);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByDniAndIdNot(String dni, Long id);

    // Nombre o email que contengan el texto (sin importar mayúsculas), o DNI exacto
    @Query("""
            select u from Usuario u
            where u.rol = :rol
              and (lower(u.nombre) like lower(concat('%', :texto, '%'))
                or lower(u.email) like lower(concat('%', :texto, '%'))
                or u.dni = :texto)
            """)
    List<Usuario> buscarPorTexto(@Param("rol") Rol rol, @Param("texto") String texto);
}
