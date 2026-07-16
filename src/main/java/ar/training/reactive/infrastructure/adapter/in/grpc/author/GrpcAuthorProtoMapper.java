package ar.training.reactive.infrastructure.adapter.in.grpc.author;

import ar.training.reactive.AuthorProto;
import ar.training.reactive.domain.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface GrpcAuthorProtoMapper {

    String UUID_TO_STRING = "uuidToString2";

    @Mapping(target = "id", source = "id", qualifiedByName = UUID_TO_STRING)
    AuthorProto toAuthorProto(Author author);

    @Named(UUID_TO_STRING)
    default String uuidToString(final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }
}
