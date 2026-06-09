package ar.training.reactive.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record BookDto(
    @NotNull(message = "id must not be null")
    UUID id,

    @NotNull(message = "isbn must not be null")
    @NotEmpty(message = "isbn must not be empty")
    @Size(max = 13, message = "isbn must not exceed 13 characters")
    String isbn,

    @NotNull(message = "title must not be null")
    @NotEmpty(message = "title must not be empty")
    @Size(max = 255, message = "title must not exceed 255 characters")
    String title,

    @Min(value = 0, message = "availableCopies must be zero or positive")
    long availableCopies
) {
}
