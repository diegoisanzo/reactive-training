package ar.training.reactive.infrastructure.adapter.out.persistence.book;

import ar.training.reactive.fixture.book.BookDtoFixture;
import ar.training.reactive.infrastructure.adapter.out.persistence.BookEntity;

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
