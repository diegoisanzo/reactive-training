package ar.training.reactive.infrastructure.adapter.in.rest.auth;

import jakarta.validation.constraints.NotEmpty;

public record LoginDto(
        @NotEmpty(message = "{login.username.NotEmpty.message}") String username,
        @NotEmpty(message = "{login.password.NotEmpty.message}") String password
) {
}
