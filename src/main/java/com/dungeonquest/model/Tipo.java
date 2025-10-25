package com.dungeonquest.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos")
public class Tipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipo;
    
    @Column(nullable = false)
    private String nombreTipo;
    
    private Integer experienciaAsociada;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    // Constructores
    public Tipo() {}
    
    public Tipo(String nombreTipo, Integer experienciaAsociada, Categoria categoria) {
        this.nombreTipo = nombreTipo;
        this.experienciaAsociada = experienciaAsociada;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public Long getIdTipo() { return idTipo; }
    public void setIdTipo(Long idTipo) { this.idTipo = idTipo; }
    
    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }
    
    public Integer getExperienciaAsociada() { return experienciaAsociada; }
    public void setExperienciaAsociada(Integer experienciaAsociada) { this.experienciaAsociada = experienciaAsociada; }
    
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
