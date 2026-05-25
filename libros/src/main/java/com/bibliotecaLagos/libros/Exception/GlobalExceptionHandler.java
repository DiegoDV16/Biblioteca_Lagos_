package com.bibliotecaLagos.libros.Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> manejarNotFound(ResourceNotFoundException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("mensaje", ex.getMessage());
        error.put("status", 404);
        error.put("fecha", LocalDateTime.now());

        return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> manejarDuplicado(DuplicateResourceException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("mensaje", ex.getMessage());
        error.put("status", 409);
        error.put("fecha", LocalDateTime.now());

        return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejarValidaciones(MethodArgumentNotValidException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put(
            "mensaje",
            ex.getBindingResult()
            .getFieldError()
            .getDefaultMessage());

        error.put("status", 400);
        error.put("fecha", LocalDateTime.now());

        return ResponseEntity
        .badRequest()
        .body(error);
    }
}