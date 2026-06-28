package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.fixture.author.AuthorFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllAuthorsUseCaseTest {

    @Mock
    private AuthorRepositoryOutboundPort authorRepositoryOutboundPort;

    @InjectMocks
    private GetAllAuthorsUseCase getAllAuthorsUseCase;

    @Test
    void shouldReturnAllAuthors() {
        var authors = List.of(AuthorFixture.withDefaults());
        when(authorRepositoryOutboundPort.findAll()).thenReturn(Flux.fromIterable(authors));

        StepVerifier.create(getAllAuthorsUseCase.getAllAuthors())
                .expectNext(authors.getFirst())
                .verifyComplete();

        verify(authorRepositoryOutboundPort).findAll();
    }
}
