package com.dungeonquest.service;

import com.dungeonquest.model.*;
import com.dungeonquest.repository.MisionRepository;
import com.dungeonquest.repository.HistorialMisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MisionService {
    
    @Autowired
    private MisionRepository misionRepository;
    
    @Autowired
    private HistorialMisionRepository historialRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    public List<Mision> obtenerTodasMisiones() {
        return misionRepository.findAll();
    }
    
    public List<Mision> obtenerMisionesDisponibles() {
        return misionRepository.findByEstado(EstadoMision.DISPONIBLE);
    }
    
    public List<Mision> obtenerMisionesDisponiblesParaUsuario(Usuario usuario) {
        return misionRepository.findMisionesDisponiblesParaRango(usuario.getRango());
    }
    
    public List<Mision> obtenerMisionesPorAventurero(Usuario aventurero) {
        return misionRepository.findByAventurero(aventurero);
    }
    
    public Optional<Mision> obtenerMisionPorId(Long id) {
        return misionRepository.findById(id);
    }
    
    public Mision crearMision(Mision mision) {
        return misionRepository.save(mision);
    }
    
    public boolean tomarMision(Long misionId, Usuario aventurero) {
        Optional<Mision> misionOpt = misionRepository.findById(misionId);
        if (misionOpt.isPresent()) {
            Mision mision = misionOpt.get();
            
            if (mision.getEstado() == EstadoMision.DISPONIBLE && 
                aventurero.getRango().ordinal() >= mision.getRangoRequerido().ordinal()) {
                
                // Guardar estado anterior para historial
                String estadoAnterior = mision.getEstado().name();
                
                mision.setAventurero(aventurero);
                mision.setEstado(EstadoMision.TOMADA);
                mision.setFechaTomada(LocalDateTime.now());
                
                misionRepository.save(mision);
                
                // Registrar en historial
                HistorialMision historial = new HistorialMision(
                    aventurero, mision, estadoAnterior, EstadoMision.TOMADA.name()
                );
                historialRepository.save(historial);
                
                return true;
            }
        }
        return false;
    }
    
    public boolean completarMision(Long misionId, Usuario aventurero) {
        Optional<Mision> misionOpt = misionRepository.findById(misionId);
        if (misionOpt.isPresent()) {
            Mision mision = misionOpt.get();
            
            if (mision.getEstado() == EstadoMision.TOMADA && 
                mision.getAventurero().getIdUsuario().equals(aventurero.getIdUsuario())) {
                
                String estadoAnterior = mision.getEstado().name();
                
                mision.setEstado(EstadoMision.COMPLETADA);
                mision.setFechaCompletada(LocalDateTime.now());
                
                misionRepository.save(mision);
                
                HistorialMision historial = new HistorialMision(
                    aventurero, mision, estadoAnterior, EstadoMision.COMPLETADA.name()
                );
                historialRepository.save(historial);
                
                return true;
            }
        }
        return false;
    }
    
    public boolean verificarMision(Long misionId, Usuario verificador) {
        Optional<Mision> misionOpt = misionRepository.findById(misionId);
        if (misionOpt.isPresent()) {
            Mision mision = misionOpt.get();
            
            if (mision.getEstado() == EstadoMision.COMPLETADA && 
                (verificador.getRol() == RolUsuario.RECEPCIONISTA || 
                 verificador.getRol() == RolUsuario.ADMINISTRADOR)) {
                
                String estadoAnterior = mision.getEstado().name();
                
                mision.setEstado(EstadoMision.VERIFICADA);
                misionRepository.save(mision);
                
                // Otorgar experiencia al aventurero
                Usuario aventurero = mision.getAventurero();
                usuarioService.actualizarRangoUsuario(aventurero.getIdUsuario(), mision.getExperiencia());
                
                HistorialMision historial = new HistorialMision(
                    verificador, mision, estadoAnterior, EstadoMision.VERIFICADA.name()
                );
                historialRepository.save(historial);
                
                return true;
            }
        }
        return false;
    }
    
    public Mision actualizarMision(Mision mision) {
        return misionRepository.save(mision);
    }
    
    public void eliminarMision(Long id) {
        misionRepository.deleteById(id);
    }
}
