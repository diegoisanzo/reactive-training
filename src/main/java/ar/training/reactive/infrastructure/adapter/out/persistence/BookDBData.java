package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.domain.model.Book;

import java.util.List;
import java.util.UUID;

public final class BookDBData {

    public static final List<Book> ALL = List.of(
        new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb7"), "9780132350884", "Clean Code"),
        new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb8"), "9780132350885", "Design Patterns"),
        new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb9"), "9780132350886", "Database Management Systems")
    );

    private BookDBData() {}
}
