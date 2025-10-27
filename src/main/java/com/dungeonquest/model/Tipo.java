package com.dungeonquest.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tipos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipo;
    
    @Column(nullable = false)
    private String nombreTipo;
    
    private Integer experienciaAsociada;
    
    @Column(columnDefinition = "TEXT")
    private String descripcionBase;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties({"tipos", "misiones"})
    private Categoria categoria;
    
    // Constructores
    public Tipo() {}
    
    public Tipo(String nombreTipo, Integer experienciaAsociada, Categoria categoria, String descripcionBase) {
        this.nombreTipo = nombreTipo;
        this.experienciaAsociada = experienciaAsociada;
        this.categoria = categoria;
        this.descripcionBase = descripcionBase;
    }
    
    // Getters y Setters
    public Long getIdTipo() { return idTipo; }
    public void setIdTipo(Long idTipo) { this.idTipo = idTipo; }
    
    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }
    
    public Integer getExperienciaAsociada() { return experienciaAsociada; }
    public void setExperienciaAsociada(Integer experienciaAsociada) { this.experienciaAsociada = experienciaAsociada; }
    
    public String getDescripcionBase() { return descripcionBase; }
    public void setDescripcionBase(String descripcionBase) { this.descripcionBase = descripcionBase; }
    
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
