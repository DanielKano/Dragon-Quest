package com.dungeonquest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_misiones")
public class HistorialMision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "mision_id", nullable = false)
    private Mision mision;
    
    @Column(nullable = false)
    private String estadoAnterior;
    
    @Column(nullable = false)
    private String estadoNuevo;
    
    @Column(nullable = false)
    private LocalDateTime fechaCambio;
    
    public HistorialMision() {
        this.fechaCambio = LocalDateTime.now();
    }
    
    public HistorialMision(Usuario usuario, Mision mision, String estadoAnterior, String estadoNuevo) {
        this();
        this.usuario = usuario;
        this.mision = mision;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Mision getMision() { return mision; }
    public void setMision(Mision mision) { this.mision = mision; }
    
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
}