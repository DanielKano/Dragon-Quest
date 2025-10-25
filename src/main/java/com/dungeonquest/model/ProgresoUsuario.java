package com.dungeonquest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progreso_usuario")
public class ProgresoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProgreso;
    
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    private Integer experienciaActual;
    
    @Enumerated(EnumType.STRING)
    private Rango rangoActual;
    
    private LocalDateTime ultimaActualizacion;
    
    // Constructores
    public ProgresoUsuario() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    public ProgresoUsuario(Usuario usuario) {
        this();
        this.usuario = usuario;
        this.experienciaActual = 0;
        this.rangoActual = Rango.F;
    }
    
    // Getters y Setters
    public Long getIdProgreso() { return idProgreso; }
    public void setIdProgreso(Long idProgreso) { this.idProgreso = idProgreso; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Integer getExperienciaActual() { return experienciaActual; }
    public void setExperienciaActual(Integer experienciaActual) { this.experienciaActual = experienciaActual; }
    
    public Rango getRangoActual() { return rangoActual; }
    public void setRangoActual(Rango rangoActual) { this.rangoActual = rangoActual; }
    
    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { 
        this.ultimaActualizacion = ultimaActualizacion; 
    }
}
