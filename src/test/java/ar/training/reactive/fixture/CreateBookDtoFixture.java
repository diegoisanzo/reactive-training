package ar.training.reactive.fixture;

import ar.training.reactive.infrastructure.adapter.in.rest.CreateBookDto;

public class CreateBookDtoFixture {

    public static CreateBookDto withDefaults() {
        return new CreateBookDto(
                "1780132350888",
                "NEW BOOK");
    }
}
