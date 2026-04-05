package com.marketplace.app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * GlobalExceptionHandler
 * 
 * Centralized exception handling for the entire Mini Marketplace application.
 * Catches and processes runtime exceptions to return appropriate HTTP responses.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles RuntimeException globally
     * Catches any RuntimeException thrown during request processing and returns error response
     * 
     * @param ex the RuntimeException that was thrown
     * @return ResponseEntity with error message and bad request status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
