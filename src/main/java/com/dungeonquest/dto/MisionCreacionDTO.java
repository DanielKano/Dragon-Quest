package com.dungeonquest.dto;

public class MisionCreacionDTO {
    private String nombre;
    private String descripcion;
    private Long categoriaId;
    private String rangoRequerido; // String form from the form, will be converted to Rango in controller
    private Integer recompensa;
    private Integer experiencia;
    private java.time.LocalDateTime fechaLimite;
    private String estado;
    private String tipo;

    public MisionCreacionDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getRangoRequerido() { return rangoRequerido; }
    public void setRangoRequerido(String rangoRequerido) { this.rangoRequerido = rangoRequerido; }

    public Integer getRecompensa() { return recompensa; }
    public void setRecompensa(Integer recompensa) { this.recompensa = recompensa; }

    public Integer getExperiencia() { return experiencia; }
    public void setExperiencia(Integer experiencia) { this.experiencia = experiencia; }

    public java.time.LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(java.time.LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}