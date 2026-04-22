package ar.training.reactive.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class Book {

    private UUID id;
    private String isbn;
    private String title;

    public Book(UUID id, String isbn, String title) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
    }

    public boolean updateFrom(Book other) {
        boolean dirty = false;
        if (!Objects.equals(getIsbn(), other.getIsbn())) {
            setIsbn(other.getIsbn());
            dirty = true;
        }
        if (!Objects.equals(getTitle(), other.getTitle())) {
            setTitle(other.getTitle());
            dirty = true;
        }
        return dirty;
    }

    public UUID getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public void setId(UUID id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Book) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isbn, title);
    }

    @Override
    public String toString() {
        return "Book[id=" + id + ", isbn=" + isbn + ", title=" + title + ']';
    }
}
