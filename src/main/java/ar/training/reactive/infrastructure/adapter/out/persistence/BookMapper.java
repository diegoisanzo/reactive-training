package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.domain.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toDomain(BookEntity entity);

    BookEntity toEntity(Book book);
}
