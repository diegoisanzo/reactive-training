package ar.training.reactive.fixture;

import ar.training.reactive.entity.Book;

import java.util.List;
import java.util.UUID;

public class BookFixture {

    public static Book withDefaults() {
        return new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb7"),
                "9780132350884",
                "Clean Code");
    }

    public static List<Book> all() {
        return List.of(
            withDefaults(),
            new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb8"), "9780132350885", "Design Patterns"),
            new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb9"), "9780132350886", "Database Management Systems")
        );
    }

}
