package com.dungeonquest.repository;

import com.dungeonquest.model.ProgresoUsuario;
import com.dungeonquest.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProgresoUsuarioRepository extends JpaRepository<ProgresoUsuario, Long> {
    Optional<ProgresoUsuario> findByUsuario(Usuario usuario);
}
