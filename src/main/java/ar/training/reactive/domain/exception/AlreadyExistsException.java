package ar.training.reactive.domain.exception;

import java.util.UUID;

public abstract class AlreadyExistsException extends IdException {

    public AlreadyExistsException(String message, UUID id) {
        super(message, id);
    }

}
