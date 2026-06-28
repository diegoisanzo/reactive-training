package ar.training.reactive.infrastructure.adapter.out.persistence.author;

import ar.training.reactive.fixture.author.AuthorDtoFixture;

public class AuthorEntityFixture {

    public static AuthorEntity withDefaults() {
        var authorDto = AuthorDtoFixture.withDefaults();

        return new AuthorEntity(authorDto.id(), authorDto.name());
    }
}
