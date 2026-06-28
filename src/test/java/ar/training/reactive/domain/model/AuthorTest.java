package ar.training.reactive.domain.model;

import ar.training.reactive.fixture.author.AuthorFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorTest {

    @Test
    void shouldUpdateFromAnotherAuthor() {
        var author = AuthorFixture.withDefaults();
        var other = AuthorFixture.withTolkienDefaults();

        boolean dirty = author.updateFrom(other);

        assertTrue(dirty);
        assertEquals(other.getName(), author.getName());
    }

    @Test
    void shouldNotBeDirtyWhenUpdatingWithSameValues() {
        var author = AuthorFixture.withDefaults();
        var other = new Author(author.getId(), author.getName());

        boolean dirty = author.updateFrom(other);

        assertFalse(dirty);
    }

    @Test
    void shouldBeEqualWhenIdsAreSame() {
        var id = UUID.randomUUID();
        var author1 = new Author(id, "name1");
        var author2 = new Author(id, "name2");

        assertEquals(author1, author2);
        assertEquals(author1.hashCode(), author2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        var author1 = new Author(UUID.randomUUID(), "name");
        var author2 = new Author(UUID.randomUUID(), "name");

        assertNotEquals(author1.getId(), author2.getId());
        assertNotEquals(author1, author2);
    }
}
