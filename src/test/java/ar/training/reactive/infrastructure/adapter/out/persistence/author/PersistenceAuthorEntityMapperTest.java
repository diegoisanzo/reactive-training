package ar.training.reactive.infrastructure.adapter.out.persistence.author;

import ar.training.reactive.fixture.author.AuthorFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PersistenceAuthorEntityMapperTest {

    private final PersistenceAuthorEntityMapper persistenceAuthorEntityMapper;

    public PersistenceAuthorEntityMapperTest() {
        this.persistenceAuthorEntityMapper = Mappers.getMapper(PersistenceAuthorEntityMapper.class);
    }

    @Test
    void mapsToAuthorEntityOk() {
        var author = AuthorFixture.withDefaults();

        var authorEntity = persistenceAuthorEntityMapper.toAuthorEntity(author);

        assertNotNull(authorEntity);
        assertEquals(author.getId(), authorEntity.getId());
        assertEquals(author.getName(), authorEntity.getName());
    }
}
