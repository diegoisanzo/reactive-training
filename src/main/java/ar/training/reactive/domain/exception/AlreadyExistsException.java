package ar.training.reactive.domain.exception;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public abstract class AlreadyExistsException extends IdException {
    public AlreadyExistsException(String message, UUID id, String title) {
        super(message, id, title);
    }
}
