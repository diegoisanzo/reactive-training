package ar.training.reactive.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("book")
public class BookEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("isbn")
    private String isbn;

    @Column("title")
    private String title;

    @Column("available_copies")
    private long availableCopies;

    public BookEntity(UUID id, String isbn, String title, long availableCopies) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.availableCopies = availableCopies;
    }

    public UUID getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public long getAvailableCopies() { return availableCopies; }
    public void setId(UUID id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAvailableCopies(long availableCopies) { this.availableCopies = availableCopies; }
}
