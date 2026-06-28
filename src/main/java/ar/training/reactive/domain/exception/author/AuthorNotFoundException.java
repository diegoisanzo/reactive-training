package ar.training.reactive.domain.exception.author;

import ar.training.reactive.domain.exception.NotFoundException;

import java.util.UUID;

public class AuthorNotFoundException extends NotFoundException {

    public AuthorNotFoundException(UUID id) {
        super("Author with id " + id + " not found", id, "Author not found");
    }

}
