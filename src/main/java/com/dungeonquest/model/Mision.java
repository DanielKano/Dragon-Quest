package com.dungeonquest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "misiones")
public class Mision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMision;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMision estado;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rango rangoRequerido;
    
    private Integer recompensa;
    private Integer experiencia;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimite;
    private LocalDateTime fechaTomada;
    private LocalDateTime fechaCompletada;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aventurero")
    private Usuario aventurero;
    
    // Constructores
    public Mision() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaLimite = LocalDateTime.now().plusMonths(1);
        this.estado = EstadoMision.DISPONIBLE;
    }
    
    public Mision(String nombre, String descripcion, Rango rangoRequerido, 
                  Integer recompensa, Integer experiencia, Categoria categoria) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rangoRequerido = rangoRequerido;
        this.recompensa = recompensa;
        this.experiencia = experiencia;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public Long getIdMision() { return idMision; }
    public void setIdMision(Long idMision) { this.idMision = idMision; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public EstadoMision getEstado() { return estado; }
    public void setEstado(EstadoMision estado) { this.estado = estado; }
    
    public Rango getRangoRequerido() { return rangoRequerido; }
    public void setRangoRequerido(Rango rangoRequerido) { this.rangoRequerido = rangoRequerido; }
    
    public Integer getRecompensa() { return recompensa; }
    public void setRecompensa(Integer recompensa) { this.recompensa = recompensa; }
    
    public Integer getExperiencia() { return experiencia; }
    public void setExperiencia(Integer experiencia) { this.experiencia = experiencia; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
    
    public LocalDateTime getFechaTomada() { return fechaTomada; }
    public void setFechaTomada(LocalDateTime fechaTomada) { this.fechaTomada = fechaTomada; }
    
    public LocalDateTime getFechaCompletada() { return fechaCompletada; }
    public void setFechaCompletada(LocalDateTime fechaCompletada) { this.fechaCompletada = fechaCompletada; }
    
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    
    public Usuario getAventurero() { return aventurero; }
    public void setAventurero(Usuario aventurero) { this.aventurero = aventurero; }
}

