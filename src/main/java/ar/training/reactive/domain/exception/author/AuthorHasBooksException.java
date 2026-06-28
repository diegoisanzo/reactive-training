package ar.training.reactive.domain.exception.author;

import ar.training.reactive.domain.exception.ConflictException;

import java.util.UUID;

public class AuthorHasBooksException extends ConflictException {

    public AuthorHasBooksException(UUID authorId) {
        super("Author with id " + authorId + " has associated books", authorId, "Author has books");
    }
}
