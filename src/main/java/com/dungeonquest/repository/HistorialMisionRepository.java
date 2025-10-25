package com.dungeonquest.repository;

import com.dungeonquest.model.HistorialMision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialMisionRepository extends JpaRepository<HistorialMision, Long> {
}
