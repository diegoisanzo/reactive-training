package ar.training.reactive.infrastructure.adapter.in.rest.book;

import ar.training.reactive.domain.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface RestBookMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    Book toBook(CreateBookDto createBookDto);

    Book toBook(BookDto bookDto);

}
