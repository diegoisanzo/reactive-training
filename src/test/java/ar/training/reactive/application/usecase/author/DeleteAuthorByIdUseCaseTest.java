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
class DeleteAuthorByIdUseCaseTest {

    @Mock
    private AuthorRepositoryOutboundPort authorRepositoryOutboundPort;

    @InjectMocks
    private DeleteAuthorByIdUseCase deleteAuthorByIdUseCase;

    @Test
    void shouldDeleteAuthorWhenExists() {
        var author = AuthorFixture.withDefaults();
        when(authorRepositoryOutboundPort.findById(author.getId())).thenReturn(Mono.just(author));
        when(authorRepositoryOutboundPort.delete(author)).thenReturn(Mono.empty());

        StepVerifier.create(deleteAuthorByIdUseCase.deleteAuthorById(author.getId()))
                .verifyComplete();

        verify(authorRepositoryOutboundPort).findById(author.getId());
        verify(authorRepositoryOutboundPort).delete(author);
    }

    @Test
    void shouldThrowExceptionWhenAuthorDoesNotExist() {
        var id = UUID.randomUUID();
        when(authorRepositoryOutboundPort.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(deleteAuthorByIdUseCase.deleteAuthorById(id))
                .expectError(AuthorNotFoundException.class)
                .verify();

        verify(authorRepositoryOutboundPort).findById(id);
    }
}
