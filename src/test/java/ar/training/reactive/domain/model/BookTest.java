package ar.training.reactive.domain.model;

import ar.training.reactive.fixture.BookFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookTest {

    @Test
    void shouldUpdateFromAnotherBook() {
        var book = BookFixture.withDefaults();
        var other = new Book(book.getId(), "new-isbn", "new-title", 99);

        boolean dirty = book.updateFrom(other);

        assertTrue(dirty);
        assertEquals("new-isbn", book.getIsbn());
        assertEquals("new-title", book.getTitle());
    }

    @Test
    void shouldNotBeDirtyWhenUpdatingWithSameValues() {
        var book = BookFixture.withDefaults();
        var other = new Book(book.getId(), book.getIsbn(), book.getTitle(), book.getAvailableCopies());

        boolean dirty = book.updateFrom(other);

        assertFalse(dirty);
    }

    @Test
    void shouldBeEqualWhenIdsAreSame() {
        var id = UUID.randomUUID();
        var book1 = new Book(id, "isbn1", "title1", 0);
        var book2 = new Book(id, "isbn2", "title2", 0);

        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        var book1 = new Book(UUID.randomUUID(), "isbn", "title", 0);
        var book2 = new Book(UUID.randomUUID(), "isbn", "title", 0);

        assertNotEquals(book1, book2);
    }
}
