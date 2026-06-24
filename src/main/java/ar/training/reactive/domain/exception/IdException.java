package ar.training.reactive.domain.exception;

import java.util.UUID;

public abstract class IdException extends RuntimeException {
    protected final UUID uuid;

    public IdException(final String message, final UUID id) {
        super(message);
        this.uuid = id;
    }

    public final UUID getId() {
        return uuid;
    }
}
