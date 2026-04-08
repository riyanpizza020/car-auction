package com.auction24.car_auction.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles RuntimeException (our custom throws)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // Handles @Valid validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // Handles everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Something went wrong");
        return ResponseEntity.internalServerError().body(response);
    }
}