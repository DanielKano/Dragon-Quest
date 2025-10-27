package com.dungeonquest.repository;

import com.dungeonquest.model.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
    List<Tipo> findByCategoria_IdCategoria(Long categoriaId);
}