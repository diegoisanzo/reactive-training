package ar.training.reactive.infrastructure.adapter.in.grpc.author;

import ar.training.reactive.AuthorProto;
import ar.training.reactive.domain.model.Author;
import ar.training.reactive.infrastructure.adapter.in.grpc.GrpcUuidMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GrpcAuthorProtoMapper extends GrpcUuidMapping {

    @Mapping(target = "id", source = "id", qualifiedByName = UUID_TO_STRING)
    AuthorProto toAuthorProto(Author author);
}
