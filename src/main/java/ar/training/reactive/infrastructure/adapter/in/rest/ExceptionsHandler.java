package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.domain.exception.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ProblemDetail.forStatus;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBookNotFound(BookNotFoundException ex) {
        var problemDetail = forStatus(NOT_FOUND);
        problemDetail.setTitle("Book not found");
        problemDetail.setDetail(ex.getMessage());

        return ResponseEntity
                .of(problemDetail)
                .build();
    }
}
