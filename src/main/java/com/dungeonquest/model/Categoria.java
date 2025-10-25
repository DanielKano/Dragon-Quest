package com.dungeonquest.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;
    
    @Column(nullable = false, unique = true)
    private String nombreCategoria;
    
    private String descripcion;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Tipo> tipos = new ArrayList<>();
    
    @OneToMany(mappedBy = "categoria")
    private List<Mision> misiones = new ArrayList<>();
    
    // Constructores
    public Categoria() {}
    
    public Categoria(String nombreCategoria, String descripcion) {
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }
    
    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public List<Tipo> getTipos() { return tipos; }
    public void setTipos(List<Tipo> tipos) { this.tipos = tipos; }
    
    public List<Mision> getMisiones() { return misiones; }
    public void setMisiones(List<Mision> misiones) { this.misiones = misiones; }
}
