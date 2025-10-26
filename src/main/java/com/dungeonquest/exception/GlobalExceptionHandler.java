package com.dungeonquest.exception;

import com.dungeonquest.dto.UsuarioRegistroDTO;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.BindException;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(UsuarioExistenteException.class)
    public String handleUsuarioExistenteException(UsuarioExistenteException ex, WebRequest request, Model model) {
        // Repoblar el DTO con los datos que el usuario ya había ingresado
        UsuarioRegistroDTO registroDTO = new UsuarioRegistroDTO();
        registroDTO.setNombre(request.getParameter("nombre"));
        registroDTO.setNombreUsuario(request.getParameter("nombreUsuario"));
        registroDTO.setEmail(request.getParameter("email"));
        // No repoblamos la contraseña por seguridad

        model.addAttribute("error", ex.getMessage());
        model.addAttribute("usuarioRegistro", registroDTO);
        return "registro";
    }
    
    @ExceptionHandler(BindException.class)
    public String handleValidationExceptions(BindException ex, Model model) {
        String errores = ex.getBindingResult()
                         .getFieldErrors()
                         .stream()
                         .map(err -> err.getField() + ": " + err.getDefaultMessage())
                         .collect(Collectors.joining(", "));
        model.addAttribute("error", "Error de validación: " + errores);
        return "error";
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolation(ConstraintViolationException ex, Model model) {
        String errores = ex.getConstraintViolations()
                         .stream()
                         .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                         .collect(Collectors.joining(", "));
        model.addAttribute("error", "Error de validación: " + errores);
        return "error";
    }
}