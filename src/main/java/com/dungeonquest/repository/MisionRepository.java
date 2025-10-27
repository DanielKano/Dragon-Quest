package com.dungeonquest.repository;

import com.dungeonquest.model.Mision;
import com.dungeonquest.model.EstadoMision;
import com.dungeonquest.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MisionRepository extends JpaRepository<Mision, Long> {
    List<Mision> findByEstado(EstadoMision estado);
    List<Mision> findByAventurero(Usuario aventurero);
    List<Mision> findByAventureroAndEstado(Usuario aventurero, EstadoMision estado);
    
    List<Mision> findByCategoria_IdCategoria(Long categoriaId);
    long countByEstado(EstadoMision estado);
}