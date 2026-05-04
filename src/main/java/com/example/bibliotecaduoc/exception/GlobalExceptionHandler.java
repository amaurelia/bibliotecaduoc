package com.example.bibliotecaduoc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de validación (@Valid falla) → 400 Bad Request
    // MethodArgumentNotValidException.class, estás diciendo: "dame el objeto que representa a esta clase"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {

        // Recorre todos los campos que fallaron la validación y arma un mensaje
        StringBuilder detalle = new StringBuilder();
        for (FieldError campo : ex.getBindingResult().getFieldErrors()) {
            detalle.append(campo.getField())           // nombre del campo (ej: "nombre")
                   .append(": ")
                   .append(campo.getDefaultMessage())  // mensaje de la anotación (ej: "no debe estar vacío")
                   .append(", ");
        }

        ApiError error = new ApiError(400, "Error de validación", detalle.toString());
        return ResponseEntity.badRequest().body(error);
    }

    // Maneja cualquier otra excepción no esperada → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericError(Exception ex) {
        ApiError error = new ApiError(500, "Error interno del servidor", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Maneja errores de la API externa (Open Library) → 404 o 502
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiError> handleWebClientError(WebClientResponseException ex) {
        if (ex.getStatusCode().value() == 404) {
            ApiError error = new ApiError(404, "ISBN no encontrado en Open Library", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        ApiError error = new ApiError(502, "Error al consultar Open Library", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }
}
