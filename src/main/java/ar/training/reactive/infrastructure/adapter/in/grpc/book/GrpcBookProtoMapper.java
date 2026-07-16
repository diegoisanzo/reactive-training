package ar.training.reactive.infrastructure.adapter.in.grpc.book;

import ar.training.reactive.BookProto;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.model.Genre;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface GrpcBookProtoMapper {

    String UUID_TO_STRING = "uuidToString";
    String GENRE_TO_STRING = "genreToString";

    @Mapping(target = "id", source = "id", qualifiedByName = UUID_TO_STRING)
    @Mapping(target = "authorId", source = "authorId", qualifiedByName = UUID_TO_STRING)
    @Mapping(target = "genre", source = "genre", qualifiedByName = GENRE_TO_STRING)
    BookProto toBookProto(Book book);

    @Named(UUID_TO_STRING)
    default String uuidToString(final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }

    @Named(GENRE_TO_STRING)
    default String genreToString(final Genre genre) {
        if (genre == null) {
            return null;
        }
        return genre.name();
    }
}
