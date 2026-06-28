package ar.training.reactive.infrastructure.adapter.out.persistence.book;

import ar.training.reactive.domain.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersistenceBookMapper {
    Book toBook(BookEntity entity);
}
