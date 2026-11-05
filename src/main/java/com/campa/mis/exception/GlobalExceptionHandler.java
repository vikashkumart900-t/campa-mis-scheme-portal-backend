
package com.campa.mis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

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
}
