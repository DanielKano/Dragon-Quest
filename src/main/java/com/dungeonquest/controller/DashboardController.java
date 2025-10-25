package com.dungeonquest.controller;

import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import com.dungeonquest.service.MisionService;
import com.dungeonquest.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private MisionService misionService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/auth/login";
        
        model.addAttribute("usuario", usuario);
        
        switch (usuario.getRol()) {
            case AVENTURERO:
                model.addAttribute("misionesActivas", 
                    misionService.obtenerMisionesPorAventurero(usuario).size());
                break;
                
            case RECEPCIONISTA:
            case ADMINISTRADOR:
                model.addAttribute("totalMisiones", 
                    misionService.obtenerTodasMisiones().size());
                model.addAttribute("misionesPendientesVerificacion", 
                    misionService.obtenerTodasMisiones().stream()
                        .filter(m -> m.getEstado().name().equals("COMPLETADA"))
                        .count());
                break;
        }
        
        return "dashboard";
    }
}
