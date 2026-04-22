package ar.training.reactive.adapter.inbound.rest;

import ar.training.reactive.domain.exception.BookNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Void> handleBookNotFound(BookNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
