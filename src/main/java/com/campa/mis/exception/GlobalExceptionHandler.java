
package com.campa.mis.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Account pending approval
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledException(
            DisabledException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            Map.of(
                "status", 403,
                "error", "ACCOUNT_NOT_APPROVED",
                "message", ex.getMessage()
            )
        );
    }

    // Invalid email or password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of(
                "status", 401,
                "error", "INVALID_CREDENTIALS",
                "message", ex.getMessage()
            )
        );
    }

    // DTO validation: @NotBlank, @Email, @Size, @Pattern
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "VALIDATION_FAILED",
                "messages", errors,
                "timestamp", LocalDateTime.now()
            )
        );
    }

    // ResponseStatusException: 400, 404, 409, etc.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex) {

        return ResponseEntity.status(ex.getStatusCode()).body(
            Map.of(
                "status", ex.getStatusCode().value(),
                "error", "REQUEST_FAILED",
                "message", ex.getReason() != null ? ex.getReason() : "Request failed",
                "timestamp", LocalDateTime.now()
            )
        );
    }

    // Invalid JSON request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "INVALID_JSON",
                "message", "Invalid or malformed JSON request",
                "timestamp", LocalDateTime.now()
            )
        );
    }

    // All unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex) {

        // Log the actual exception on the backend
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of(
                "status", 500,
                "error", "INTERNAL_SERVER_ERROR",
                "message", "Something went wrong. Please try again later.",
                "timestamp", LocalDateTime.now()
            )
        );
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "BAD_REQUEST",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now()
            )
        );
    }
    
    public static ResponseEntity<Map<String, Object>> unauthorizedResponse(
            String message, String path) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of(
                "status", 401,
                "error", "UNAUTHORIZED",
                "message", message,
                "path", path,
                "timestamp", LocalDateTime.now()
            )
        );
    }
}
