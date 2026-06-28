package ar.training.reactive.domain.exception.author;

import ar.training.reactive.domain.exception.AlreadyExistsException;

import java.util.UUID;

public class AuthorAlreadyExistsException extends AlreadyExistsException {

    public AuthorAlreadyExistsException(UUID id) {
        super("Author with id " + id + " already exists", id, "Author already exists");
    }

}
