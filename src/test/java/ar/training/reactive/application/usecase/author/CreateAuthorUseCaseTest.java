package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorAlreadyExistsException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAuthorUseCaseTest {

    @Mock
    private AuthorRepositoryOutboundPort authorRepositoryOutboundPort;

    @InjectMocks
    private CreateAuthorUseCase createAuthorUseCase;

    @Test
    void shouldCreateBookWhenItDoesNotExist() {
        var author = AuthorFixture.withDefaults();
        when(authorRepositoryOutboundPort.findById(author.getId())).thenReturn(Mono.empty());
        when(authorRepositoryOutboundPort.save(any(Author.class))).thenReturn(Mono.just(author));

        StepVerifier.create(createAuthorUseCase.createAuthor(author))
                .expectNext(author)
                .verifyComplete();

        verify(authorRepositoryOutboundPort).findById(author.getId());
        verify(authorRepositoryOutboundPort).save(author);
    }

    @Test
    void shouldThrowExceptionWhenBookAlreadyExists() {
        var author = AuthorFixture.withDefaults();
        when(authorRepositoryOutboundPort.findById(author.getId())).thenReturn(Mono.just(author));
        // Mocking save because .then() evaluates its argument during assembly
        when(authorRepositoryOutboundPort.save(any(Author.class))).thenReturn(Mono.empty());

        StepVerifier.create(createAuthorUseCase.createAuthor(author))
                .expectError(AuthorAlreadyExistsException.class)
                .verify();

        verify(authorRepositoryOutboundPort).findById(author.getId());
    }
}
