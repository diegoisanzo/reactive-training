package ar.training.reactive.infrastructure.adapter.out.persistence.book;

import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.model.Genre;

import java.util.List;
import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData.ERICH_GAMMA;
import static ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData.RAGHU_RAMAKRISHNAN;
import static ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData.ROBERT_MARTIN;

public final class BookDBData {

    public static final List<Book> ALL = List.of(
        new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb7"), "9780132350884", "Clean Code", 10, Genre.TECHNOLOGY, ROBERT_MARTIN.getId()),
        new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb8"), "9780132350885", "Design Patterns", 5, Genre.TECHNOLOGY, ERICH_GAMMA.getId()),
        new Book(UUID.fromString("93d68169-2722-427f-8556-0762db939eb9"), "9780132350886", "Database Management Systems", 3, Genre.TECHNOLOGY, RAGHU_RAMAKRISHNAN.getId())
    );

    private BookDBData() {}
}
