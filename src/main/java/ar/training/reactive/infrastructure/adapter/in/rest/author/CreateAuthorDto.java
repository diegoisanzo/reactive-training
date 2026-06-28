package ar.training.reactive.infrastructure.adapter.in.rest.author;

import jakarta.validation.constraints.NotBlank;

public record CreateAuthorDto(
    @NotBlank(message = "name must not be blank")
    String name
) {
}
