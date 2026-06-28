package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.domain.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestAuthorDtoMapper {
    AuthorDto toAuthorDto(Author author);
}
