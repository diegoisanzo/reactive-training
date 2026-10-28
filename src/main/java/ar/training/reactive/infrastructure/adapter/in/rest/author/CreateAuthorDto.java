package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.domain.model.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAuthorDto(
    @NotBlank(message = "{author.name.NotBlank.message}")
    @Size(max = Author.NAME_MAX_LENGTH, message = "{author.name.Size.message}")
    String name
) {
}
