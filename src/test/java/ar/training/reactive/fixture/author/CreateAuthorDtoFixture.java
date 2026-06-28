package ar.training.reactive.fixture.author;

import ar.training.reactive.infrastructure.adapter.in.rest.author.CreateAuthorDto;

public class CreateAuthorDtoFixture {

    public static CreateAuthorDto withDefaults() {
        return new CreateAuthorDto("Jane Austen");
    }
}
