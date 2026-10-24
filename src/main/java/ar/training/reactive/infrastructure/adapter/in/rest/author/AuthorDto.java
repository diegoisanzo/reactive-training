package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.domain.model.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AuthorDto(
    @NotNull(message = "id must not be null")
    UUID id,

    @NotBlank(message = "name must not be blank")
    @Size(max = Author.NAME_MAX_LENGTH, message = "name must not exceed " + Author.NAME_MAX_LENGTH + " characters")
    String name
) {
}
