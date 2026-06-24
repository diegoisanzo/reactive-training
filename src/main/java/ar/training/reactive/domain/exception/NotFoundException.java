package ar.training.reactive.domain.exception;

import java.util.UUID;

public abstract class NotFoundException extends IdException {

    private final String title;

    protected NotFoundException(String message, UUID id, String title) {
        super(message, id);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
