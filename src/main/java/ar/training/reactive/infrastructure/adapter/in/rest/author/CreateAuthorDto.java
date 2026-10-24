package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.domain.model.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAuthorDto(
    @NotBlank(message = "name must not be blank")
    @Size(max = Author.NAME_MAX_LENGTH, message = "name must not exceed " + Author.NAME_MAX_LENGTH + " characters")
    String name
) {
}
