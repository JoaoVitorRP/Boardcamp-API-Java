package com.boardcamp.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionsHandler {
    @ExceptionHandler({ ConflictException.class })
    public ResponseEntity<Object> handleConflict(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<Object> handleNotFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({ UnprocessableEntityException.class })
    public ResponseEntity<Object> handleUnprocessableEntity(UnprocessableEntityException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }
}
