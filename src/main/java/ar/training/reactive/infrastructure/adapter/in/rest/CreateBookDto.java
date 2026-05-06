package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.domain.model.Book;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookDto(
    @NotNull(message = "isbn must not be null")
    @NotEmpty(message = "isbn must not be empty")
    @Size(max = 13, message = "isbn must not exceed 13 characters")
    String isbn,

    @NotNull(message = "title must not be null")
    @NotEmpty(message = "title must not be empty")
    @Size(max = 255, message = "title must not exceed 255 characters")
    String title
) {

    public static CreateBookDto of(Book book) {
        return new CreateBookDto(
            book.getIsbn(),
            book.getTitle()
        );
    }
}
