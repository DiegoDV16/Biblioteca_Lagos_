package com.bibliotecaLagos.Roles.Exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> manejarNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> manejarDuplicado(DuplicateResourceException ex) {
        log.warn("Conflicto por duplicado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(
                    MethodArgumentNotValidException ex) {
        log.warn("Error de validacion en los datos de entrada");

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        log.warn("Errores de validacion: {}", errores);
        return ResponseEntity
                .badRequest()
                .body(errores);
    }
}