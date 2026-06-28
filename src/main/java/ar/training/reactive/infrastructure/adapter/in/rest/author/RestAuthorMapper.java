package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.domain.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface RestAuthorMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    Author toAuthor(CreateAuthorDto createAuthorDto);

    Author toAuthor(AuthorDto authorDto);
}
