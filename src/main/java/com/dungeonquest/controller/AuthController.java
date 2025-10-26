package com.dungeonquest.controller;

import com.dungeonquest.model.Usuario;
import com.dungeonquest.model.RolUsuario;
import com.dungeonquest.service.UsuarioService;
import com.dungeonquest.dto.UsuarioLoginDTO;
import com.dungeonquest.dto.UsuarioRegistroDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        
        // Rol por defecto: AVENTURERO
        Usuario usuario = new Usuario(registroDTO.getNombreUsuario(),
                                   registroDTO.getEmail(),
                                   registroDTO.getPassword(),
                                   RolUsuario.AVENTURERO);
        usuario.setNombre(registroDTO.getNombre());
        usuarioService.registrarUsuario(usuario); // Esto lanzará UsuarioExistenteException si es necesario
        model.addAttribute("success", "Usuario registrado exitosamente");
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout() {
        // Spring Security maneja el logout. Este método es un placeholder si se necesita.
        return "redirect:/auth/login?logout";
    }
}
