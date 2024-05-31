package com.ricardo.movilApp.repository;

import com.ricardo.movilApp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByEmail(String email);
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.contrasena = :password")
    Usuario findByEmailAndContrasena(@Param("email") String email, @Param("password") String password);

}
