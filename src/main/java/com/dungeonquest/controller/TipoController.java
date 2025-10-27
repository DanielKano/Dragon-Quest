package com.dungeonquest.controller;

import com.dungeonquest.model.Tipo;
import com.dungeonquest.repository.TipoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos")
public class TipoController {

    private final TipoRepository tipoRepository;

    public TipoController(TipoRepository tipoRepository) {
        this.tipoRepository = tipoRepository;
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Tipo>> obtenerTiposPorCategoria(@PathVariable Long categoriaId) {
        try {
            List<Tipo> tipos = tipoRepository.findByCategoria_IdCategoria(categoriaId);
            if (tipos == null) {
                return ResponseEntity.ok(List.of()); // Return empty list instead of null
            }
            return ResponseEntity.ok(tipos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tipo> obtenerTipo(@PathVariable Long id) {
        return tipoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}