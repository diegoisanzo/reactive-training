package ar.training.reactive.domain.exception.book;

import ar.training.reactive.domain.exception.AlreadyExistsException;

import java.util.UUID;

public class BookAlreadyExistsException extends AlreadyExistsException {

    public BookAlreadyExistsException(UUID id) {
        super("Book with id " + id + " already exists", id);
    }

}
