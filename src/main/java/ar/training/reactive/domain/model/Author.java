package ar.training.reactive.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class Author {

    private UUID id;
    private String name;

    public Author(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean updateFrom(Author other) {
        boolean dirty = false;
        if (!Objects.equals(getName(), other.getName())) {
            setName(other.getName());
            dirty = true;
        }
        return dirty;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Author) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Author[id=" + id + ", name=" + name + ']';
    }
}
