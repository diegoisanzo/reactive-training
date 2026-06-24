package ar.training.reactive.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

public abstract class BaseEntity<ID> implements Persistable<ID> {

    @Transient
    private boolean isNew = true;

    public void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
