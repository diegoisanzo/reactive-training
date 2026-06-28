package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import ar.training.reactive.fixture.author.AuthorFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAuthorByIdUseCaseTest {

    @Mock
    private AuthorRepositoryOutboundPort authorRepositoryOutboundPort;

    @InjectMocks
    private GetAuthorByIdUseCase getAuthorByIdUseCase;

    @Test
    void shouldReturnAuthorWhenExists() {
        var author = AuthorFixture.withDefaults();
        when(authorRepositoryOutboundPort.findById(author.getId())).thenReturn(Mono.just(author));

        StepVerifier.create(getAuthorByIdUseCase.getAuthorById(author.getId()))
                .expectNext(author)
                .verifyComplete();

        verify(authorRepositoryOutboundPort).findById(author.getId());
    }

    @Test
    void shouldThrowExceptionWhenAuthorDoesNotExist() {
        var id = UUID.randomUUID();
        when(authorRepositoryOutboundPort.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(getAuthorByIdUseCase.getAuthorById(id))
                .expectError(AuthorNotFoundException.class)
                .verify();

        verify(authorRepositoryOutboundPort).findById(id);
    }
}
