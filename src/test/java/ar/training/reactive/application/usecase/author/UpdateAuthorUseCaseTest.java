package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import ar.training.reactive.domain.model.Author;
import ar.training.reactive.fixture.author.AuthorFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAuthorUseCaseTest {

    @Mock
    private AuthorRepositoryOutboundPort authorRepositoryOutboundPort;

    @InjectMocks
    private UpdateAuthorUseCase updateAuthorUseCase;

    @Test
    void shouldUpdateAuthorWhenExistsAndDataChanged() {
        var existingAuthor = AuthorFixture.withDefaults();
        var incomingAuthor = new Author(existingAuthor.getId(), "New Name");

        when(authorRepositoryOutboundPort.findById(existingAuthor.getId())).thenReturn(Mono.just(existingAuthor));
        when(authorRepositoryOutboundPort.save(any(Author.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(updateAuthorUseCase.updateAuthor(incomingAuthor))
                .expectNextMatches(updated ->
                        updated.getId().equals(existingAuthor.getId()) &&
                        updated.getName().equals("New Name")
                )
                .verifyComplete();

        verify(authorRepositoryOutboundPort).findById(existingAuthor.getId());
        verify(authorRepositoryOutboundPort).save(any(Author.class));
    }

    @Test
    void shouldNotSaveWhenDataHasNotChanged() {
        var existingAuthor = AuthorFixture.withDefaults();
        var incomingAuthor = new Author(existingAuthor.getId(), existingAuthor.getName());

        when(authorRepositoryOutboundPort.findById(existingAuthor.getId())).thenReturn(Mono.just(existingAuthor));

        StepVerifier.create(updateAuthorUseCase.updateAuthor(incomingAuthor))
                .expectNext(existingAuthor)
                .verifyComplete();

        verify(authorRepositoryOutboundPort).findById(existingAuthor.getId());
        verify(authorRepositoryOutboundPort, never()).save(any(Author.class));
    }

    @Test
    void shouldThrowExceptionWhenAuthorDoesNotExist() {
        var author = AuthorFixture.withDefaults();
        when(authorRepositoryOutboundPort.findById(author.getId())).thenReturn(Mono.empty());

        StepVerifier.create(updateAuthorUseCase.updateAuthor(author))
                .expectError(AuthorNotFoundException.class)
                .verify();

        verify(authorRepositoryOutboundPort).findById(author.getId());
    }
}
