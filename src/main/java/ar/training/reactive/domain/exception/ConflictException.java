package ar.training.reactive.domain.exception;

import java.util.UUID;

public abstract class ConflictException extends IdException {
    public ConflictException(String message, UUID id, String title) {
        super(message, id, title);
    }
}
