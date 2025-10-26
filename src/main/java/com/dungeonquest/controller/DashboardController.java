package com.dungeonquest.controller;

import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import com.dungeonquest.model.EstadoMision;
import com.dungeonquest.service.MisionService;
import com.dungeonquest.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class DashboardController {
    
    private final MisionService misionService;
    private final UsuarioService usuarioService;
    
    public DashboardController(MisionService misionService, UsuarioService usuarioService) {
        this.misionService = misionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorNombre(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
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
                    misionService.contarMisionesPorEstado(EstadoMision.COMPLETADA));
                break;
        }
        
        return "dashboard";
    }
}
