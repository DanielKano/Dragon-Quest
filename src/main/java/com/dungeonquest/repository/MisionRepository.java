package com.dungeonquest.repository;

import com.dungeonquest.model.Mision;
import com.dungeonquest.model.EstadoMision;
import com.dungeonquest.model.Rango;
import com.dungeonquest.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MisionRepository extends JpaRepository<Mision, Long> {
    List<Mision> findByEstado(EstadoMision estado);
    List<Mision> findByRangoRequerido(Rango rangoRequerido);
    List<Mision> findByAventurero(Usuario aventurero);
    List<Mision> findByAventureroAndEstado(Usuario aventurero, EstadoMision estado);
    
    @Query("SELECT m FROM Mision m WHERE m.estado = 'DISPONIBLE' AND m.rangoRequerido <= :rangoUsuario")
    List<Mision> findMisionesDisponiblesParaRango(@Param("rangoUsuario") Rango rangoUsuario);
    
    List<Mision> findByCategoriaIdCategoria(Long categoriaId);
    long countByEstado(EstadoMision estado);
}
