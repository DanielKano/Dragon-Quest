package com.dungeonquest.repository;

import com.dungeonquest.model.Rango;
import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(RolUsuario rol);
    List<Usuario> findByRango(Rango rango);
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByEmail(String email);
}
