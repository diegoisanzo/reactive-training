package ar.training.reactive.dto;

import ar.training.reactive.entity.Book;

import java.util.UUID;

public record BookDto(UUID id, String ISBN, String title) {

    public static BookDto of(Book book) {
        return new BookDto(
            book.getId(),
            book.getIsbn(),
            book.getTitle()
        );
    }

}
