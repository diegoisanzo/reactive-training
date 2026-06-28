package ar.training.reactive.domain.exception;

import java.util.UUID;

public abstract class AlreadyExistsException extends ConflictException {
    public AlreadyExistsException(String message, UUID id, String title) {
        super(message, id, title);
    }
}
