package com.recrutement.offreservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(
            IllegalStateException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            IllegalArgumentException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(
            AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
    }


    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignException(
            FeignException ex) {

        if (ex.status() == 401) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentification refusée par le User-Service"));
        }

        if (ex.status() == 403) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Accès refusé par le User-Service"));
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Impossible de vérifier le CV auprès du User-Service"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        Class<?> requiredType = ex.getRequiredType();

        if (requiredType != null && requiredType.isEnum()) {
            String allowedValues = Arrays.stream(requiredType.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Valeur invalide pour le paramètre '"
                            + ex.getName()
                            + "'. Valeurs autorisées : " + allowedValues
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "message", "Valeur invalide pour le paramètre '" + ex.getName() + "'"
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Données invalides");

        return ResponseEntity
                .badRequest()
                .body(Map.of("message", message));
    }

}