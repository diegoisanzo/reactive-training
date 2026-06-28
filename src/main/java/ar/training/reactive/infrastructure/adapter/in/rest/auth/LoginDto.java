package ar.training.reactive.infrastructure.adapter.in.rest.auth;

import jakarta.validation.constraints.NotEmpty;

public record LoginDto(
        @NotEmpty String username,
        @NotEmpty String password
) {
}
