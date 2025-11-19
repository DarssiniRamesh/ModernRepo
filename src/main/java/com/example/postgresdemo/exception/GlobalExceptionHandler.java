package com.example.postgresdemo.exception;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Catches specific exceptions and returns appropriate HTTP responses with meaningful error messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * PUBLIC_INTERFACE
     * Handles PropertyReferenceException thrown when an invalid sort property is specified.
     * Returns 400 Bad Request with details about valid properties.
     * 
     * @param ex the PropertyReferenceException
     * @return ResponseEntity with error details and 400 status
     */
    // PUBLIC_INTERFACE
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, Object>> handlePropertyReferenceException(PropertyReferenceException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Invalid sort property: " + ex.getPropertyName() + ". Valid properties are: id, title, description, createdAt, updatedAt");
        body.put("details", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * PUBLIC_INTERFACE
     * Handles IllegalArgumentException which can occur from invalid pagination or sort parameters.
     * Returns 400 Bad Request with error details.
     * 
     * @param ex the IllegalArgumentException
     * @return ResponseEntity with error details and 400 status
     */
    // PUBLIC_INTERFACE
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Invalid request parameter: " + ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * PUBLIC_INTERFACE
     * Handles MethodArgumentTypeMismatchException for invalid parameter types.
     * Returns 400 Bad Request with error details.
     * 
     * @param ex the MethodArgumentTypeMismatchException
     * @return ResponseEntity with error details and 400 status
     */
    // PUBLIC_INTERFACE
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Invalid parameter '" + ex.getName() + "': " + ex.getValue() + " could not be converted to " + ex.getRequiredType().getSimpleName());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * PUBLIC_INTERFACE
     * Handles generic exceptions that weren't caught by more specific handlers.
     * Returns 500 Internal Server Error.
     * 
     * @param ex the Exception
     * @return ResponseEntity with error details and 500 status
     */
    // PUBLIC_INTERFACE
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred. Please contact support if the problem persists.");
        
        // Log the full exception for debugging (in production, log to file)
        ex.printStackTrace();
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
