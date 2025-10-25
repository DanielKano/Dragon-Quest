package com.dungeonquest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_mision")
public class HistorialMision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aventurero_id")
    private Usuario aventurero;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_id")
    private Mision mision;
    
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fechaCambio;
    
    // Constructores
    public HistorialMision() {
        this.fechaCambio = LocalDateTime.now();
    }
    
    public HistorialMision(Usuario aventurero, Mision mision, String estadoAnterior, String estadoNuevo) {
        this();
        this.aventurero = aventurero;
        this.mision = mision;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }
    
    // Getters y Setters
    public Long getIdHistorial() { return idHistorial; }
    public void setIdHistorial(Long idHistorial) { this.idHistorial = idHistorial; }
    
    public Usuario getAventurero() { return aventurero; }
    public void setAventurero(Usuario aventurero) { this.aventurero = aventurero; }
    
    public Mision getMision() { return mision; }
    public void setMision(Mision mision) { this.mision = mision; }
    
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
}
