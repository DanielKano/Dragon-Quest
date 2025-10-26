package com.dungeonquest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(unique = true, nullable = false)
    private String nombreUsuario;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rango rango;
    
    private Integer experienciaActual;
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "aventurero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mision> misionesTomadas = new ArrayList<>();
    
    // Constructores
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
        this.experienciaActual = 0;
        this.rango = Rango.F;
    }
    
    public Usuario(String nombreUsuario, String email, String password, RolUsuario rol) {
        this();
        this.nombre = nombreUsuario; // Por defecto, usar nombreUsuario como nombre
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }
    
    // Getters y Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    
    public Rango getRango() { return rango; }
    public void setRango(Rango rango) { this.rango = rango; }
    
    public Integer getExperienciaActual() { return experienciaActual; }
    public void setExperienciaActual(Integer experienciaActual) { 
        this.experienciaActual = experienciaActual; 
    }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Mision> getMisionesTomadas() { return misionesTomadas; }
    public void setMisionesTomadas(List<Mision> misionesTomadas) { this.misionesTomadas = misionesTomadas; }
}
