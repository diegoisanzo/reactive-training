package ar.training.reactive.infrastructure.adapter.in.grpc.book;

import ar.training.reactive.fixture.book.BookFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GrpcBookProtoMapperTest {

    private final GrpcBookProtoMapper grpcBookProtoMapper;

    public GrpcBookProtoMapperTest() {
        this.grpcBookProtoMapper = Mappers.getMapper(GrpcBookProtoMapper.class);
    }

    @Test
    void mapsToBookProtoOk() {
        var book = BookFixture.withDefaults();

        var bookProto = grpcBookProtoMapper.toBookProto(book);

        assertNotNull(bookProto);

        assertEquals(book.getId().toString(), bookProto.getId());
        assertEquals(book.getIsbn(), bookProto.getIsbn());
        assertEquals(book.getTitle(), bookProto.getTitle());
        assertEquals(book.getAvailableCopies(), bookProto.getAvailableCopies());
        assertEquals(book.getGenre().name(), bookProto.getGenre());
        assertEquals(book.getAuthorId().toString(), bookProto.getAuthorId());
    }
}
