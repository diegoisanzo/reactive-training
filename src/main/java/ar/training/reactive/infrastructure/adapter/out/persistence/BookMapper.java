package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.domain.model.Book;

public final class BookMapper {

    private BookMapper() {}

    public static Book toDomain(BookEntity entity) {
        return new Book(entity.getId(), entity.getIsbn(), entity.getTitle());
    }

    public static BookEntity toEntity(Book book) {
        return new BookEntity(book.getId(), book.getIsbn(), book.getTitle());
    }
}
