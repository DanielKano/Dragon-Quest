package com.dungeonquest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioLoginDTO {
    
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String nombreUsuario;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    // Getters y Setters
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}