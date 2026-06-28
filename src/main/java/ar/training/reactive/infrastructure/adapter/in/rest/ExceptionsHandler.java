package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.domain.exception.ConflictException;
import ar.training.reactive.domain.exception.NotFoundException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.Map;

import static ar.training.reactive.infrastructure.adapter.in.rest.ExceptionsHandler.ExceptionHandlerUtils.nullToLiteralString;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ProblemDetail.forStatus;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ConflictException ex) {
        var problemDetail = forStatus(CONFLICT);
        problemDetail.setTitle(ex.getTitle());
        problemDetail.setDetail(ex.getMessage());

        return ResponseEntity
                .of(problemDetail)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NotFoundException ex) {
        var problemDetail = forStatus(NOT_FOUND);
        problemDetail.setTitle(ex.getTitle());
        problemDetail.setDetail(ex.getMessage());

        return ResponseEntity
                .of(problemDetail)
                .build();
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ProblemDetail> handleRateLimitExceeded(RequestNotPermitted ex) {
        var problemDetail = forStatus(TOO_MANY_REQUESTS);
        problemDetail.setTitle("Rate limit exceeded");
        return ResponseEntity.of(problemDetail).build();
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

    static class ExceptionHandlerUtils {
        static Object nullToLiteralString(Object value) {
            if (value == null) {
                return "null";
            }
            return value;
        }
    }
}
