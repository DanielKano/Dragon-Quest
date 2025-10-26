package com.dungeonquest.controller;

import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import com.dungeonquest.service.UsuarioService;
import com.dungeonquest.dto.UsuarioLoginDTO;
import com.dungeonquest.dto.UsuarioRegistroDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("usuarioLogin", new UsuarioLoginDTO());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("usuarioLogin") UsuarioLoginDTO loginDTO,
                       BindingResult result,
                       HttpSession session, 
                       Model model) {
        if (result.hasErrors()) {
            return "login";
        }
        
        Optional<Usuario> usuarioOpt = usuarioService.autenticarUsuario(loginDTO.getNombreUsuario(), loginDTO.getPassword());
        
        if (usuarioOpt.isPresent()) {
            session.setAttribute("usuario", usuarioOpt.get());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuarioRegistro", new UsuarioRegistroDTO());
        return "registro";
    }
    
    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuarioRegistro") UsuarioRegistroDTO registroDTO,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return "registro";
        }
        
        try {
            // Rol por defecto: AVENTURERO
            Usuario usuario = new Usuario(registroDTO.getNombreUsuario(), 
                                       registroDTO.getEmail(), 
                                       registroDTO.getPassword(), 
                                       RolUsuario.AVENTURERO);
            usuario.setNombre(registroDTO.getNombre());
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
