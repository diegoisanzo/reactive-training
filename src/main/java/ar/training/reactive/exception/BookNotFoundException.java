package ar.training.reactive.exception;

import java.util.UUID;

public class BookNotFoundException extends RuntimeException {

    private final UUID id;

    public BookNotFoundException(UUID id) {
        super("Book with id " + id + " not found");
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
