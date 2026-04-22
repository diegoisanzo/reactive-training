package ar.training.reactive.fixture;

import ar.training.reactive.db.BookDBData;
import ar.training.reactive.entity.Book;

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
