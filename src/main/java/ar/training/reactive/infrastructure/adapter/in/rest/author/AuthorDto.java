package ar.training.reactive.infrastructure.adapter.in.rest.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AuthorDto(
    @NotNull(message = "id must not be null")
    UUID id,

    @NotBlank(message = "name must not be blank")
    String name
) {
}
