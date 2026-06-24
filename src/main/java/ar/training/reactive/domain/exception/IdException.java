package ar.training.reactive.domain.exception;

import java.util.UUID;

public abstract class IdException extends RuntimeException {
    protected final UUID uuid;
    protected final String title;

    public IdException(final String message, final UUID id, String title) {
        super(message);
        this.uuid = id;
        this.title = title;
    }

    public final UUID getId() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }
}
