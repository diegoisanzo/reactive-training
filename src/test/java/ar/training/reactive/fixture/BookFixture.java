package ar.training.reactive.fixture;

import ar.training.reactive.domain.model.Book;
import ar.training.reactive.infrastructure.adapter.out.persistence.BookDBData;

import java.util.List;

public class BookFixture {

    public static Book withDefaults() {
        return copy(BookDBData.ALL.getFirst());
    }

    public static List<Book> all() {
        return BookDBData.ALL.stream().map(BookFixture::copy).toList();
    }

    private static Book copy(Book b) {
        return new Book(b.getId(), b.getIsbn(), b.getTitle());
    }
}
