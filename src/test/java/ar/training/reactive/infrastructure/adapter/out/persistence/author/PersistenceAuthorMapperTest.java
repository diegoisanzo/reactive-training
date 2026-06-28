package ar.training.reactive.infrastructure.adapter.out.persistence.author;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PersistenceAuthorMapperTest {

    private final PersistenceAuthorMapper persistenceAuthorMapper;

    public PersistenceAuthorMapperTest() {
        this.persistenceAuthorMapper = Mappers.getMapper(PersistenceAuthorMapper.class);
    }

    @Test
    void mapsToAuthorOk() {
        var authorEntity = AuthorEntityFixture.withDefaults();

        var author = persistenceAuthorMapper.toAuthor(authorEntity);

        assertNotNull(author);
        assertEquals(authorEntity.getId(), author.getId());
        assertEquals(authorEntity.getName(), author.getName());
    }
}
