package com.dungeonquest.controller;

import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import com.dungeonquest.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String nombreUsuario, 
                       @RequestParam String password, 
                       HttpSession session, 
                       Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.autenticarUsuario(nombreUsuario, password);
        
        if (usuarioOpt.isPresent()) {
            session.setAttribute("usuario", usuarioOpt.get());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }
    
    @PostMapping("/registro")
    public String registrar(@RequestParam String nombreUsuario,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam RolUsuario rol,
                           Model model) {
        try {
            Usuario usuario = new Usuario(nombreUsuario, email, password, rol);
            usuarioService.registrarUsuario(usuario);
            model.addAttribute("success", "Usuario registrado exitosamente");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
