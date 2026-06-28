package ar.training.reactive.infrastructure.adapter.in.rest.book;

import ar.training.reactive.domain.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestBookDtoMapper {
    BookDto toBookDto(Book book);
}
