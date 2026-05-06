package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.domain.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toBook(BookEntity entity);

    BookEntity toBookEntity(Book book);
}
