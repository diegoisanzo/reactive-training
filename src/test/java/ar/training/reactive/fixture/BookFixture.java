package ar.training.reactive.fixture;

import ar.training.reactive.db.BookSeedData;
import ar.training.reactive.entity.Book;

import java.util.List;

public class BookFixture {

    public static Book withDefaults() {
        return copy(BookSeedData.ALL.get(0));
    }

    public static List<Book> all() {
        return BookSeedData.ALL.stream().map(BookFixture::copy).toList();
    }

    private static Book copy(Book b) {
        return new Book(b.getId(), b.getIsbn(), b.getTitle());
    }

}
