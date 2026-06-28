package ar.training.reactive.infrastructure.adapter.out.persistence.author;

import ar.training.reactive.domain.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersistenceAuthorEntityMapper {
    AuthorEntity toAuthorEntity(Author author);
}
