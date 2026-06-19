package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.domain.exception.BookAlreadyExistsException;
import ar.training.reactive.domain.exception.BookNotFoundException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.ProblemDetail.forStatus;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleBookAlreadyExists(BookAlreadyExistsException ex) {
        var problemDetail = forStatus(CONFLICT);
        problemDetail.setTitle("Book already exists");
        problemDetail.setDetail(ex.getMessage());

        return ResponseEntity
                .of(problemDetail)
                .build();
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBookNotFound(BookNotFoundException ex) {
        var problemDetail = forStatus(NOT_FOUND);
        problemDetail.setTitle("Book not found");
        problemDetail.setDetail(ex.getMessage());

        return ResponseEntity
                .of(problemDetail)
                .build();
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ProblemDetail> handleRateLimitExceeded(RequestNotPermitted ex) {
        var detail = forStatus(TOO_MANY_REQUESTS);
        detail.setTitle("Rate limit exceeded");
        return ResponseEntity.of(detail).build();
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "rejectedValue", nullToLiteralString(error.getRejectedValue()),
                        "message", nullToLiteralString(error.getDefaultMessage())
                ))
                .toList();

        var problemDetail = forStatus(BAD_REQUEST);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("Input validation failed");
        problemDetail.setInstance(URI.create(exchange.getRequest().getPath().toString()));
        problemDetail.setProperty("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

    private static Object nullToLiteralString(Object value) {
        if (value == null) {
            return "null";
        }
        return value;
    }
}
