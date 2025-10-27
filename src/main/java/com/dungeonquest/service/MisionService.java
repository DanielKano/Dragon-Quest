package com.dungeonquest.service;

import com.dungeonquest.model.*;
import com.dungeonquest.repository.MisionRepository;
import org.springframework.transaction.annotation.Transactional;
import com.dungeonquest.repository.HistorialMisionRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MisionService {
    
    private final MisionRepository misionRepository;
    private final HistorialMisionRepository historialRepository;
    private final UsuarioService usuarioService;
    
    public MisionService(MisionRepository misionRepository, HistorialMisionRepository historialRepository, UsuarioService usuarioService) {
        this.misionRepository = misionRepository;
        this.historialRepository = historialRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<Mision> obtenerTodasMisiones() {
        List<Mision> misiones = misionRepository.findAll();
        // Forzar la inicialización de las categorías para evitar LazyInitializationException en la vista
        misiones.forEach(mision -> {
            if (mision.getCategoria() != null) {
                mision.getCategoria().getNombreCategoria();
            }
        });
        return misiones;
    }
    
    @Transactional(readOnly = true)
    public List<Mision> obtenerMisionesDisponibles() {
        try {
            List<Mision> misiones = Optional.ofNullable(
                misionRepository.findByEstado(EstadoMision.DISPONIBLE)
            ).orElse(List.of());
    
            // Forzar carga de categorías (evita LazyInitialization)
            misiones.forEach(m -> {
                try {
                    if (m.getCategoria() != null) {
                        m.getCategoria().getNombreCategoria();
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error al inicializar categoría de misión: " + m.getIdMision());
                }
            });
    
            return misiones;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar misiones disponibles: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Mision> obtenerMisionesDisponiblesParaUsuario(Usuario usuario) {
        if (usuario == null || usuario.getRango() == null) {
            throw new IllegalArgumentException("Usuario o rango no válido");
        }
        
        // Obtener y forzar carga de misiones disponibles
        List<Mision> misionesDisponibles = obtenerMisionesDisponibles();
        
        // Filtrar por rango en Java
        return misionesDisponibles.stream()
                .filter(mision -> mision.getRangoRequerido() != null)
                .filter(mision -> usuario.getRango().ordinal() >= mision.getRangoRequerido().ordinal())
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<Mision> obtenerMisionesPorAventurero(Usuario aventurero) {
        return misionRepository.findByAventurero(aventurero);
    }
    
    public Optional<Mision> obtenerMisionPorId(Long id) {
        return misionRepository.findById(id);
    }
    
    public Mision crearMision(Mision mision) {
        return misionRepository.save(mision);
    }
    
    @Transactional
    public void guardarMision(Mision mision) {
        misionRepository.save(mision);
    }
    
    @Transactional
    public void tomarMision(Long misionId, Usuario aventurero) {
        Mision mision = misionRepository.findById(misionId)
                .orElseThrow(() -> new IllegalArgumentException("Misión no encontrada con ID: " + misionId));

        if (mision.getEstado() != EstadoMision.DISPONIBLE) {
            throw new IllegalStateException("La misión '" + mision.getNombre() + "' ya no está disponible.");
        }

        if (aventurero.getRango().ordinal() < mision.getRangoRequerido().ordinal()) {
            throw new IllegalArgumentException("El aventurero " + aventurero.getNombreUsuario() + " no tiene el rango suficiente ("
                    + mision.getRangoRequerido() + ") para esta misión.");
        }

        // Guardar estado anterior para historial
        String estadoAnterior = mision.getEstado().name();

        mision.setAventurero(aventurero);
        mision.setEstado(EstadoMision.TOMADA);
        mision.setFechaTomada(LocalDateTime.now());

        misionRepository.save(mision);

        // Registrar en historial
        HistorialMision historial = new HistorialMision(
                aventurero, mision, estadoAnterior, EstadoMision.TOMADA.name());
        historialRepository.save(historial);
    }
    
    public boolean completarMision(Long misionId, Usuario aventurero) {
        Optional<Mision> misionOpt = misionRepository.findById(misionId);
        if (misionOpt.isPresent()) {
            Mision mision = misionOpt.get();
            
            // Validar estado y aventurero asignado
            if (mision.getEstado() == EstadoMision.TOMADA && mision.getAventurero() != null &&
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
            
            // Validar que la misión tenga un aventurero asignado
            Usuario aventureroMision = mision.getAventurero();
            if (aventureroMision == null) return false;

            if (mision.getEstado() == EstadoMision.COMPLETADA && 
                (verificador.getRol() == RolUsuario.RECEPCIONISTA || 
                 verificador.getRol() == RolUsuario.ADMINISTRADOR)) {
                
                String estadoAnterior = mision.getEstado().name();
                
                mision.setEstado(EstadoMision.VERIFICADA);
                misionRepository.save(mision);
                
                // Otorgar experiencia al aventurero
                usuarioService.actualizarRangoUsuario(aventureroMision.getIdUsuario(), mision.getExperiencia());
                
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

    public long contarMisionesPorEstado(EstadoMision estado) {
        return misionRepository.countByEstado(estado);
    }
}
