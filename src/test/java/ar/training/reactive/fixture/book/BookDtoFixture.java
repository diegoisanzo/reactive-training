package ar.training.reactive.fixture.book;

import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.infrastructure.adapter.in.rest.book.BookDto;
import ar.training.reactive.infrastructure.adapter.in.rest.book.RestBookDtoMapper;
import org.mapstruct.factory.Mappers;

public class BookDtoFixture {

    private static final RestBookDtoMapper mapper =
            Mappers.getMapper(RestBookDtoMapper.class);

    public static BookDto withDefaults() {
        var defaultBook = BookFixture.withDefaults();
        return mapper.toBookDto(defaultBook);
    }

    public static BookDto withUpdatesToDefault() {
        return new BookDto(
                withDefaults().id(),
                "9780132350888",
                "Clean Code: Updated",
                20,
                Genre.TECHNOLOGY,
                withDefaults().authorId());
    }
}
