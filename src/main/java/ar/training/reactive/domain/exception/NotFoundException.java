package ar.training.reactive.domain.exception;

import java.util.UUID;

public abstract class NotFoundException extends IdException {
    protected NotFoundException(String message, UUID id, String title) {
        super(message, id, title);
    }
}
