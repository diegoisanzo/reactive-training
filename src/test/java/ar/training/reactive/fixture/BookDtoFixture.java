package ar.training.reactive.fixture;

import ar.training.reactive.dto.BookDto;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;

@DataR2dbcTest
public class BookDtoFixture {

    public static BookDto withDefaults() {
        var defaultBook = BookFixture.withDefaults();

        return BookDto.of(defaultBook);
    }

    public static BookDto withUpdatesToDefault() {
        return new BookDto(
                withDefaults().id(),
                "9780132350888",
                "Clean Code: Updated");
    }

}
