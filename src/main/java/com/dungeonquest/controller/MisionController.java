package com.dungeonquest.controller;

import com.dungeonquest.dto.MisionCreacionDTO;
import com.dungeonquest.model.*;
import com.dungeonquest.service.MisionService;
import com.dungeonquest.service.UsuarioService;
import com.dungeonquest.repository.CategoriaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/misiones")
public class MisionController {
    
    private final MisionService misionService;
    private final UsuarioService usuarioService;
    private final CategoriaRepository categoriaRepository;
    
    public MisionController(MisionService misionService, UsuarioService usuarioService, CategoriaRepository categoriaRepository) {
        this.misionService = misionService;
        this.usuarioService = usuarioService;
        this.categoriaRepository = categoriaRepository;
    }
    @GetMapping
    public String listarMisiones(Principal principal, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorNombre(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Mision> misiones;
        if (usuario.getRol() == RolUsuario.AVENTURERO) {
            misiones = misionService.obtenerMisionesDisponiblesParaUsuario(usuario);
        } else {
            misiones = misionService.obtenerTodasMisiones();
        }
        
        model.addAttribute("misiones", misiones);
        model.addAttribute("usuario", usuario);
        return "misiones/lista";
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
        model.addAttribute("mision", new MisionCreacionDTO());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("rangos", Rango.values());
        return "misiones/formulario";
    }
    
    @PostMapping("/nueva")
    public String crearMision(@Valid @ModelAttribute("mision") MisionCreacionDTO misionDTO,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            // Si hay errores, volvemos a cargar los datos necesarios para el formulario
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("rangos", Rango.values());
            return "misiones/formulario";
        }

        Optional<Categoria> categoriaOpt = categoriaRepository.findById(misionDTO.getCategoriaId());
        if (categoriaOpt.isPresent()) {
            Mision mision = new Mision();
            mision.setNombre(misionDTO.getNombre());
            mision.setDescripcion(misionDTO.getDescripcion());
            try {
                mision.setRangoRequerido(Rango.valueOf(misionDTO.getRangoRequerido().toUpperCase()));
            } catch (Exception ex) {
                result.rejectValue("rangoRequerido", "error.rangoRequerido", "Rango no válido");
                model.addAttribute("categorias", categoriaRepository.findAll());
                model.addAttribute("rangos", Rango.values());
                return "misiones/formulario";
            }
            mision.setRecompensa(misionDTO.getRecompensa());
            mision.setExperiencia(misionDTO.getExperiencia());
            mision.setFechaLimite(misionDTO.getFechaLimite());
            mision.setCategoria(categoriaOpt.get());
            misionService.crearMision(mision);
        } else {
            // Manejar el caso en que la categoría no exista
            result.rejectValue("categoriaId", "error.mision", "La categoría seleccionada no es válida.");
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("rangos", Rango.values());
            return "misiones/formulario";
        }
        
        return "redirect:/misiones";
    }
    
    @PostMapping("/{id}/tomar")
    public String tomarMision(@PathVariable Long id, Principal principal) {
        Usuario usuario = usuarioService.obtenerUsuarioPorNombre(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        misionService.tomarMision(id, usuario);
        return "redirect:/misiones/mis-misiones";
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
