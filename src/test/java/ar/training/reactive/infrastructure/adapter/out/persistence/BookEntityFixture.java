package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.fixture.BookDtoFixture;

public class BookEntityFixture {

    public static BookEntity withDefaults() {
        var bookDto = BookDtoFixture.withDefaults();

        return new BookEntity(bookDto.id(),
                bookDto.isbn(),
                bookDto.title(),
                bookDto.availableCopies(),
                bookDto.genre()
        );
    }

}
