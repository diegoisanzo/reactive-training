package ar.training.reactive.infrastructure.adapter.out.persistence.book;

import ar.training.reactive.infrastructure.adapter.out.persistence.PersistenceBookMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PersistenceBookMapperTest {

    private final PersistenceBookMapper persistenceBookMapper;

    public PersistenceBookMapperTest() {
        this.persistenceBookMapper = Mappers.getMapper(PersistenceBookMapper.class);
    }

    @Test
    void mapsToBookOk() {
        var bookEntity = BookEntityFixture.withDefaults();

        var book = persistenceBookMapper.toBook(bookEntity);

        assertNotNull(book);

        assertEquals(bookEntity.getId(), book.getId());
        assertEquals(bookEntity.getIsbn(), book.getIsbn());
        assertEquals(bookEntity.getTitle(), book.getTitle());
    }
}
