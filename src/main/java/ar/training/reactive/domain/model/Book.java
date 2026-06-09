package ar.training.reactive.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class Book {

    private UUID id;
    private String isbn;
    private String title;
    private long availableCopies;

    public Book(UUID id, String isbn, String title, long availableCopies) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.availableCopies = availableCopies;
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
        if (getAvailableCopies() != other.getAvailableCopies()) {
            setAvailableCopies(other.getAvailableCopies());
            dirty = true;
        }
        return dirty;
    }

    public UUID getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public long getAvailableCopies() { return availableCopies; }
    public void setId(UUID id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAvailableCopies(long availableCopies) { this.availableCopies = availableCopies; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Book) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Book[id=" + id + ", isbn=" + isbn + ", title=" + title + ", availableCopies=" + availableCopies + ']';
    }
}
