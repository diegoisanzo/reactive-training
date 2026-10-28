package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.domain.model.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AuthorDto(
    @NotNull(message = "{common.id.NotNull.message}")
    UUID id,

    @NotBlank(message = "{author.name.NotBlank.message}")
    @Size(max = Author.NAME_MAX_LENGTH, message = "{author.name.Size.message}")
    String name
) {
}
