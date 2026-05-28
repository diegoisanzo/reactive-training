package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.fixture.BookFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PersistenceBookEntityMapperTest {

    private final PersistenceBookEntityMapper persistenceBookEntityMapper;

    public PersistenceBookEntityMapperTest() {
        this.persistenceBookEntityMapper = Mappers.getMapper(PersistenceBookEntityMapper.class);
    }

    @Test
    void mapsToBookEntityOk() {
        var book = BookFixture.withDefaults();

        var bookEntity = persistenceBookEntityMapper.toBookEntity(book);

        assertNotNull(bookEntity);

        assertEquals(book.getId(), bookEntity.getId());
        assertEquals(book.getIsbn(), bookEntity.getIsbn());
        assertEquals(book.getTitle(), bookEntity.getTitle());
    }
}
