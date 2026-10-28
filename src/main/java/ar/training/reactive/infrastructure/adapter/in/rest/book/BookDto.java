package ar.training.reactive.infrastructure.adapter.in.rest.book;

import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.model.Genre;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record BookDto(
    @NotNull(message = "{common.id.NotNull.message}")
    UUID id,

    @NotNull(message = "{book.isbn.NotNull.message}")
    @NotEmpty(message = "{book.isbn.NotEmpty.message}")
    @Size(max = 13, message = "{book.isbn.Size.message}")
    String isbn,

    @NotNull(message = "{book.title.NotNull.message}")
    @NotEmpty(message = "{book.title.NotEmpty.message}")
    @Size(max = Book.TITLE_MAX_LENGTH, message = "{book.title.Size.message}")
    String title,

    @PositiveOrZero(message = "{book.availableCopies.PositiveOrZero.message}")
    long availableCopies,

    @NotNull(message = "{book.genre.NotNull.message}")
    Genre genre,

    @NotNull(message = "{book.authorId.NotNull.message}")
    UUID authorId
) {
}
