package com.noxas.reparaciones.repository;

import com.noxas.reparaciones.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Usado por Spring Security en el login del panel admin
    Optional<Usuario> findByUsername(String username);
}