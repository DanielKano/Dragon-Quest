package com.dungeonquest.controller;

import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import com.dungeonquest.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserManagementController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/usuarios")
    public String gestionUsuarios(HttpSession session, Model model) {
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null || admin.getRol() != RolUsuario.ADMINISTRADOR) {
            return "redirect:/dashboard";
        }
        
        List<Usuario> usuarios = usuarioService.obtenerTodosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }
    
    @PostMapping("/usuarios/{id}/cambiar-rol")
    public String cambiarRolUsuario(@PathVariable Long id, 
                                   @RequestParam RolUsuario nuevoRol,
                                   HttpSession session,
                                   Model model) {
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null || admin.getRol() != RolUsuario.ADMINISTRADOR) {
            return "redirect:/dashboard";
        }
        
        boolean exito = usuarioService.cambiarRolUsuario(id, nuevoRol);
        if (exito) {
            model.addAttribute("success", "Rol actualizado exitosamente");
        } else {
            model.addAttribute("error", "Error al actualizar el rol");
        }
        
        return "redirect:/admin/usuarios?success=" + exito;
    }
}
