package com.dungeonquest.controller;

import com.dungeonquest.model.*;
import com.dungeonquest.service.MisionService;
import com.dungeonquest.service.UsuarioService;
import com.dungeonquest.repository.CategoriaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/misiones")
public class MisionController {
    
    @Autowired
    private MisionService misionService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @GetMapping
    public String listarMisiones(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/auth/login";
        
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
    public String misMisiones(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/auth/login";
        
        List<Mision> misMisiones = misionService.obtenerMisionesPorAventurero(usuario);
        model.addAttribute("misiones", misMisiones);
        model.addAttribute("usuario", usuario);
        return "misiones/mis-misiones";
    }
    
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaMision(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || 
            (usuario.getRol() != RolUsuario.RECEPCIONISTA && 
             usuario.getRol() != RolUsuario.ADMINISTRADOR)) {
            return "redirect:/misiones";
        }
        
        model.addAttribute("mision", new Mision());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("rangos", Rango.values());
        return "misiones/formulario";
    }
    
    @PostMapping("/nueva")
    public String crearMision(@ModelAttribute Mision mision,
                             @RequestParam Long categoriaId,
                             HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/auth/login";
        
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(categoriaId);
        if (categoriaOpt.isPresent()) {
            mision.setCategoria(categoriaOpt.get());
            misionService.crearMision(mision);
        }
        
        return "redirect:/misiones";
    }
    
    @PostMapping("/{id}/tomar")
    public String tomarMision(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != RolUsuario.AVENTURERO) {
            return "redirect:/misiones";
        }
        
        misionService.tomarMision(id, usuario);
        return "redirect:/misiones/mis-misiones";
    }
    
    @PostMapping("/{id}/completar")
    public String completarMision(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != RolUsuario.AVENTURERO) {
            return "redirect:/misiones";
        }
        
        misionService.completarMision(id, usuario);
        return "redirect:/misiones/mis-misiones";
    }
    
    @PostMapping("/{id}/verificar")
    public String verificarMision(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || 
            (usuario.getRol() != RolUsuario.RECEPCIONISTA && 
             usuario.getRol() != RolUsuario.ADMINISTRADOR)) {
            return "redirect:/misiones";
        }
        
        misionService.verificarMision(id, usuario);
        return "redirect:/misiones";
    }
}
