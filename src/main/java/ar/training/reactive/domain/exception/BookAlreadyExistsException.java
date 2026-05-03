package ar.training.reactive.domain.exception;

import java.util.UUID;

public class BookAlreadyExistsException extends RuntimeException {

    private final UUID id;

    public BookAlreadyExistsException(UUID id) {
        super("Book with id " + id + " already exists");
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
