package ar.training.reactive.fixture.author;

import ar.training.reactive.domain.model.Author;

import java.util.UUID;

public class AuthorFixture {

    public static Author withDefaults() {
        return new Author(UUID.fromString("93d68169-2722-427f-8556-0762db939ea0"), "Jane Austen");
    }

    public static Author withTolkienDefaults() {
        return new Author(UUID.fromString("93d68169-2722-427f-8556-ffffeeeedddd"), "J. R. R. Tolkien");
    }
}
