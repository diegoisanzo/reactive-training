package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.domain.exception.BookNotFoundException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
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

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", (Object) error.getField(),
                        "rejectedValue", error.getRejectedValue(),
                        "message", error.getDefaultMessage()
                ))
                .toList();

        var problemDetail = forStatus(BAD_REQUEST);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("Input validation failed");
        problemDetail.setInstance(URI.create(exchange.getRequest().getPath().toString()));
        problemDetail.setProperty("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

}
