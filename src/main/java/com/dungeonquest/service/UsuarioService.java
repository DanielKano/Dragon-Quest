package com.dungeonquest.service;

import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import com.dungeonquest.model.Rango;
import com.dungeonquest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByNombreUsuario(usuario.getNombreUsuario())) {
            throw new RuntimeException("Nombre de usuario ya existe");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }
        
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
    
    public Optional<Usuario> autenticarUsuario(String nombreUsuario, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent() && passwordEncoder.matches(password, usuarioOpt.get().getPassword())) {
            return usuarioOpt;
        }
        return Optional.empty();
    }
    
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public List<Usuario> obtenerUsuariosPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }
    
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    public boolean actualizarRangoUsuario(Long usuarioId, Integer experienciaGanada) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            int nuevaExperiencia = (usuario.getExperienciaActual() == null ? 0 : usuario.getExperienciaActual()) + experienciaGanada;
            usuario.setExperienciaActual(nuevaExperiencia);
            
            // Actualizar rango basado en experiencia
            Rango nuevoRango = calcularRangoPorExperiencia(nuevaExperiencia);
            if (nuevoRango != usuario.getRango()) {
                usuario.setRango(nuevoRango);
            }
            
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }
    
    private Rango calcularRangoPorExperiencia(int experiencia) {
        if (experiencia >= 2101) return Rango.S;
        else if (experiencia >= 1501) return Rango.A;
        else if (experiencia >= 1001) return Rango.B;
        else if (experiencia >= 601) return Rango.C;
        else if (experiencia >= 301) return Rango.D;
        else if (experiencia >= 101) return Rango.E;
        else return Rango.F;
    }
}
