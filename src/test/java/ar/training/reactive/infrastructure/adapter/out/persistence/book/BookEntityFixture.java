package ar.training.reactive.infrastructure.adapter.out.persistence.book;

import ar.training.reactive.fixture.book.BookDtoFixture;

public class BookEntityFixture {

    public static BookEntity withDefaults() {
        var bookDto = BookDtoFixture.withDefaults();

        return new BookEntity(bookDto.id(),
                bookDto.isbn(),
                bookDto.title(),
                bookDto.availableCopies(),
                bookDto.genre(),
                bookDto.authorId()
        );
    }

}
