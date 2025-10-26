package com.dungeonquest.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
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