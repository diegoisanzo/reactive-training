package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.fixture.BookFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class BookMapperTest {

    private final BookMapper bookMapper;

    public BookMapperTest() {
        this.bookMapper = Mappers.getMapper(BookMapper.class);
    }

    @Test
    void mapsToBookOk() {
        var bookEntity = BookEntityFixture.withDefaults();

        var book = bookMapper.toBook(bookEntity);

        assertNotNull(book);

        assertEquals(bookEntity.getId(), book.getId());
        assertEquals(bookEntity.getIsbn(), book.getIsbn());
        assertEquals(bookEntity.getTitle(), book.getTitle());
    }

    @Test
    void mapsToBookEntityOk() {
        var book = BookFixture.withDefaults();

        var bookEntity = bookMapper.toBookEntity(book);

        assertNotNull(bookEntity);

        assertEquals(book.getId(), bookEntity.getId());
        assertEquals(book.getIsbn(), bookEntity.getIsbn());
        assertEquals(book.getTitle(), bookEntity.getTitle());
    }
}
