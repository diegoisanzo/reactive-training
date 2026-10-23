package ar.training.reactive.infrastructure.adapter.in.grpc.book;

import ar.training.reactive.BookProto;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.infrastructure.adapter.in.grpc.GrpcUuidMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface GrpcBookProtoMapper extends GrpcUuidMapping {

    String GENRE_TO_STRING = "genreToString";

    @Mapping(target = "id", source = "id", qualifiedByName = UUID_TO_STRING)
    @Mapping(target = "authorId", source = "authorId", qualifiedByName = UUID_TO_STRING)
    @Mapping(target = "genre", source = "genre", qualifiedByName = GENRE_TO_STRING)
    BookProto toBookProto(Book book);

    @Named(GENRE_TO_STRING)
    default String genreToString(final Genre genre) {
        if (genre == null) {
            return null;
        }
        return genre.name();
    }
}
