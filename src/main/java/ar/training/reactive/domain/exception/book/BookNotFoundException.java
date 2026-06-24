package ar.training.reactive.domain.exception.book;

import ar.training.reactive.domain.exception.NotFoundException;

import java.util.UUID;

public class BookNotFoundException extends NotFoundException {

    public BookNotFoundException(UUID id) {
        super("Book with id " + id + " not found", id, "Book not found");
    }

}
