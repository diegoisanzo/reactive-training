package ar.training.reactive.infrastructure.adapter.in.grpc.author;

import ar.training.reactive.fixture.author.AuthorFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GrpcAuthorProtoMapperTest {

    private final GrpcAuthorProtoMapper grpcAuthorProtoMapper;

    public GrpcAuthorProtoMapperTest() {
        this.grpcAuthorProtoMapper = Mappers.getMapper(GrpcAuthorProtoMapper.class);
    }

    @Test
    void mapsToAuthorProtoOk() {
        var author = AuthorFixture.withDefaults();

        var authorProto = grpcAuthorProtoMapper.toAuthorProto(author);

        assertNotNull(authorProto);

        assertEquals(author.getId().toString(), authorProto.getId());
        assertEquals(author.getName(), authorProto.getName());
    }
}
