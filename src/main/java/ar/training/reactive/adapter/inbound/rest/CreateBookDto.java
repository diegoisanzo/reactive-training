package ar.training.reactive.adapter.inbound.rest;

import ar.training.reactive.domain.model.Book;

public record CreateBookDto(String isbn, String title) {

    public static CreateBookDto of(Book book) {
        return new CreateBookDto(
            book.getIsbn(),
            book.getTitle()
        );
    }
}
