package com.dungeonquest.controller;

import com.dungeonquest.model.*;
import com.dungeonquest.service.MisionService;
import com.dungeonquest.service.UsuarioService;
import com.dungeonquest.repository.CategoriaRepository;
import com.dungeonquest.repository.TipoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.security.Principal;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/misiones")
public class MisionController {
    private static final Logger logger = LoggerFactory.getLogger(MisionController.class);
    
    private final MisionService misionService;
    private final UsuarioService usuarioService;
    private final CategoriaRepository categoriaRepository;
    private final TipoRepository tipoRepository;
    
    public MisionController(MisionService misionService, UsuarioService usuarioService, 
                          CategoriaRepository categoriaRepository, TipoRepository tipoRepository) {
        this.misionService = misionService;
        this.usuarioService = usuarioService;
        this.categoriaRepository = categoriaRepository;
        this.tipoRepository = tipoRepository;
    }
    @GetMapping
    public String listarMisiones(Principal principal, Model model) {
        try {
            if (principal == null) {
                logger.error("Principal es null - redirigiendo a login");
                return "redirect:/auth/login";
            }
            
            String username = principal.getName();
            logger.info("Listando misiones para usuario: {}", username);
            
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorNombre(username);
            if (usuarioOpt.isEmpty()) {
                logger.error("Usuario no encontrado en la base de datos: {}", username);
                model.addAttribute("error", "Usuario no encontrado en el sistema");
                return "error";
            }
            
            Usuario usuario = usuarioOpt.get();
            logger.info("Usuario encontrado - ID: {}, Nombre: {}, Rol: {}", 
                       usuario.getIdUsuario(), usuario.getNombreUsuario(), usuario.getRol());
            
            List<Mision> misiones;
            try {
                if (usuario.getRol() == RolUsuario.AVENTURERO) {
                    logger.info("Obteniendo misiones disponibles para el aventurero ID: {}", usuario.getIdUsuario());
                    misiones = misionService.obtenerMisionesDisponiblesParaUsuario(usuario);
                } else {
                    logger.info("Obteniendo todas las misiones para rol: {}", usuario.getRol());
                    misiones = misionService.obtenerTodasMisiones();
                    // Para el formulario de asignación, necesitamos la lista de aventureros
                    if (usuario.getRol() == RolUsuario.RECEPCIONISTA || usuario.getRol() == RolUsuario.ADMINISTRADOR) {
                        model.addAttribute("aventureros", usuarioService.obtenerUsuariosPorRol(RolUsuario.AVENTURERO));
                    }
                }
                
                logger.info("Misiones encontradas: {}", misiones.size());
                if (misiones.isEmpty()) {
                    logger.info("No se encontraron misiones para mostrar");
                    model.addAttribute("infoMessage", "No hay misiones disponibles en este momento");
                }
                
                model.addAttribute("misiones", misiones);
                model.addAttribute("usuario", usuario);
                
                return "misiones/lista";
                
            } catch (Exception e) {
                logger.error("Error al obtener misiones de la base de datos: ", e);
                model.addAttribute("error", "Error al obtener las misiones");
                model.addAttribute("message", "Por favor, inténtelo de nuevo más tarde");
                return "error";
            }
            
        } catch (Exception e) {
            logger.error("Error crítico al listar misiones: ", e);
            model.addAttribute("error", "Error interno del servidor");
            model.addAttribute("message", "Por favor, contacte al administrador del sistema");
            return "error";
        }
    }
    
    @GetMapping("/mis-misiones")
    public String misMisiones(Principal principal, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorNombre(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Mision> misMisiones = misionService.obtenerMisionesPorAventurero(usuario);
        model.addAttribute("misiones", misMisiones);
        model.addAttribute("usuario", usuario);
        return "misiones/mis-misiones";
    }
    
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaMision(Model model) {
        // La autorización ya la hace Spring Security
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "misiones/formulario";
    }
    
    @PostMapping("/nueva")
    public String crearMision(@RequestParam("categoriaId") Long categoriaId,
                             @RequestParam("tipoId") Long tipoId,
                             @RequestParam("nombre") String nombre,
                             @RequestParam("experiencia") Integer experiencia,
                             @RequestParam("rangoRequerido") String rangoRequerido,
                             @RequestParam("recompensa") Integer recompensa,
                             @RequestParam("descripcion") String descripcion,
                             Model model) {
        try {
            // Validar campos requeridos
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es requerido");
            }
            if (experiencia == null || experiencia < 0) {
                throw new IllegalArgumentException("La experiencia debe ser un número positivo");
            }
            if (recompensa == null || recompensa < 0) {
                throw new IllegalArgumentException("La recompensa debe ser un número positivo");
            }
            if (rangoRequerido == null || rangoRequerido.trim().isEmpty()) {
                throw new IllegalArgumentException("El rango es requerido");
            }
            if (descripcion == null || descripcion.trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción es requerida");
            }
            Optional<Categoria> categoriaOpt = categoriaRepository.findById(categoriaId);
            Optional<Tipo> tipoOpt = tipoRepository.findById(tipoId);
            
            if (categoriaOpt.isEmpty()) {
                model.addAttribute("error", "Categoría no encontrada");
                model.addAttribute("categorias", categoriaRepository.findAll());
                return "misiones/formulario";
            }

            if (tipoOpt.isEmpty()) {
                model.addAttribute("error", "Tipo no encontrado");
                model.addAttribute("categorias", categoriaRepository.findAll());
                return "misiones/formulario";
            }
            
            Rango rango;
            try {
                rango = Rango.valueOf(rangoRequerido.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Rango no válido: " + rangoRequerido);
                model.addAttribute("categorias", categoriaRepository.findAll());
                return "misiones/formulario";
            }
            
            Mision mision = new Mision(
                nombre,
                descripcion,
                rango,
                recompensa,
                experiencia,
                categoriaOpt.get()
            );
            
            logger.info("Creando misión: {}", mision.getNombre());
            mision = misionService.crearMision(mision);
            logger.info("Misión creada con ID: {}", mision.getIdMision());
            
            return "redirect:/misiones";
            
        } catch (Exception ex) {
            logger.error("Error al crear misión", ex);
            String errorMsg;
            if (ex instanceof IllegalArgumentException) {
                errorMsg = "Datos no válidos: " + ex.getMessage();
            } else {
                errorMsg = "Error al crear la misión. Por favor, verifica todos los campos: " + ex.getMessage();
            }
            model.addAttribute("error", errorMsg);
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "misiones/formulario";
        }
    }
    
    @GetMapping("/tomar/{id}")
    public String tomarMision(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            // 1️⃣ Verificar autenticación
            if (principal == null) {
                redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para tomar una misión.");
                return "redirect:/auth/login";
            }
    
            // 2️⃣ Obtener usuario autenticado
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorNombre(principal.getName());
            if (usuarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/misiones";
            }
            Usuario usuario = usuarioOpt.get();
    
            // 3️⃣ Buscar la misión
            Optional<Mision> misionOpt = misionService.obtenerMisionPorId(id);
            if (misionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Misión no encontrada.");
                return "redirect:/misiones";
            }
            Mision mision = misionOpt.get();
    
            // 4️⃣ Verificar estado
            if (mision.getEstado() != EstadoMision.DISPONIBLE) {
                redirectAttributes.addFlashAttribute("error", "La misión ya fue tomada por otro aventurero.");
                return "redirect:/misiones";
            }
    
            // 5️⃣ Asignar la misión al usuario y guardar
            misionService.tomarMision(id, usuario);
    
            // 7️⃣ Redirigir con mensaje
            redirectAttributes.addFlashAttribute("mensaje", "¡Has tomado la misión: " + mision.getNombre() + "!");
            return "redirect:/misiones/mis-misiones";
    
        } catch (Exception e) {
            logger.error("Error al tomar la misión", e);
            redirectAttributes.addFlashAttribute("error", "Error al tomar la misión.");
            return "redirect:/misiones";
        }
    }
    
    @PostMapping("/{id}/asignar")
    public String asignarMision(@PathVariable Long id,
                                @RequestParam("aventureroId") Long aventureroId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        // La autorización de rol ya la hace Spring Security
        try {
            Usuario aventurero = usuarioService.obtenerUsuarioPorId(aventureroId)
                    .orElseThrow(() -> new IllegalArgumentException("Aventurero no encontrado."));

            if (aventurero.getRol() != RolUsuario.AVENTURERO) {
                throw new IllegalArgumentException("El usuario seleccionado no es un aventurero.");
            }

            misionService.tomarMision(id, aventurero);
            redirectAttributes.addFlashAttribute("mensaje", "Misión asignada exitosamente a " + aventurero.getNombreUsuario() + ".");
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Intento fallido de asignar misión: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("Error al asignar misión", e);
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al intentar asignar la misión.");
        }
        return "redirect:/misiones";
    }

    @PostMapping("/{id}/completar")
    public String completarMision(@PathVariable Long id, Principal principal) {
        Usuario usuario = usuarioService.obtenerUsuarioPorNombre(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        misionService.completarMision(id, usuario);
        return "redirect:/misiones/mis-misiones";
    }
    
    @PostMapping("/{id}/verificar")
    public String verificarMision(@PathVariable Long id, Principal principal) {
        Usuario usuario = usuarioService.obtenerUsuarioPorNombre(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        misionService.verificarMision(id, usuario);
        return "redirect:/misiones";
    }
}
