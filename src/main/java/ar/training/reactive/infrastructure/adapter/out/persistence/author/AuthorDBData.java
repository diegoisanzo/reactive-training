package ar.training.reactive.infrastructure.adapter.out.persistence.author;

import ar.training.reactive.domain.model.Author;

import java.util.List;
import java.util.UUID;

public final class AuthorDBData {

    public static final Author ROBERT_MARTIN     = new Author(UUID.fromString("a1d68169-2722-427f-8556-0762db939ea1"), "Robert C. Martin");
    public static final Author ERICH_GAMMA       = new Author(UUID.fromString("a1d68169-2722-427f-8556-0762db939ea2"), "Erich Gamma");
    public static final Author RAGHU_RAMAKRISHNAN = new Author(UUID.fromString("a1d68169-2722-427f-8556-0762db939ea3"), "Raghu Ramakrishnan");

    public static final List<Author> ALL = List.of(ROBERT_MARTIN, ERICH_GAMMA, RAGHU_RAMAKRISHNAN);

    private AuthorDBData() {}
}
