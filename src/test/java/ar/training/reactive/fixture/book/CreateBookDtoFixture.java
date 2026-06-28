package ar.training.reactive.fixture.book;

import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.infrastructure.adapter.in.rest.book.CreateBookDto;
import ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData;

public class CreateBookDtoFixture {

    public static CreateBookDto withDefaults() {
        return new CreateBookDto(
                "1780132350888",
                "NEW BOOK",
                7,
                Genre.FICTION,
                AuthorDBData.ALL.getFirst().getId());
    }
}
