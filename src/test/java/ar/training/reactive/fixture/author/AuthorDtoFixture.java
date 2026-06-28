package ar.training.reactive.fixture.author;

import ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorDto;
import ar.training.reactive.infrastructure.adapter.in.rest.author.RestAuthorDtoMapper;
import org.mapstruct.factory.Mappers;

public class AuthorDtoFixture {

    private static final RestAuthorDtoMapper mapper =
            Mappers.getMapper(RestAuthorDtoMapper.class);

    public static AuthorDto withDefaults() {
        return mapper.toAuthorDto(AuthorFixture.withDefaults());
    }
}
