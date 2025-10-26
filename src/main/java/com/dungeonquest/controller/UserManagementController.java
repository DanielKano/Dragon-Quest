package com.dungeonquest.controller;

import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import com.dungeonquest.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserManagementController {
    
    private final UsuarioService usuarioService;

    @Autowired
    public UserManagementController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        // La autorización ya la hace Spring Security
        List<Usuario> usuarios = usuarioService.obtenerTodosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }
    
    @PostMapping("/usuarios/{id}/cambiar-rol")
    public String cambiarRolUsuario(@PathVariable Long id, 
                                   @RequestParam RolUsuario nuevoRol,
                                   RedirectAttributes redirectAttributes) {
        // La autorización ya la hace Spring Security
        boolean exito = usuarioService.cambiarRolUsuario(id, nuevoRol);
        if (exito) {
            redirectAttributes.addFlashAttribute("success", "Rol actualizado exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el rol del usuario.");
        }
        
        return "redirect:/admin/usuarios";
    }
}
