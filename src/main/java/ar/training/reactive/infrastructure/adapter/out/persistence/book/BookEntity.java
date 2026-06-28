package ar.training.reactive.infrastructure.adapter.out.persistence.book;

import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.infrastructure.adapter.out.persistence.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("book")
public class BookEntity extends BaseEntity<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("isbn")
    private String isbn;

    @Column("title")
    private String title;

    @Column("available_copies")
    private long availableCopies;

    @Column("genre")
    private Genre genre;

    @Column("author_id")
    private UUID authorId;

    public BookEntity(UUID id, String isbn, String title, long availableCopies, Genre genre, UUID authorId) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.availableCopies = availableCopies;
        this.genre = genre;
        this.authorId = authorId;
    }

    @Override
    public UUID getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public long getAvailableCopies() { return availableCopies; }
    public Genre getGenre() { return genre; }
    public UUID getAuthorId() { return authorId; }
    public void setId(UUID id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAvailableCopies(long availableCopies) { this.availableCopies = availableCopies; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
}
