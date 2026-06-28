package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import ar.training.reactive.domain.exception.book.BookAlreadyExistsException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.fixture.author.AuthorFixture;
import ar.training.reactive.fixture.book.BookFixture;
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
class CreateBookUseCaseTest {

    @Mock
    private BookRepositoryOutboundPort bookRepositoryOutboundPort;

    @Mock
    private AuthorRepositoryOutboundPort authorRepositoryOutboundPort;

    @InjectMocks
    private CreateBookUseCase createBookUseCase;

    @Test
    void shouldCreateBookWhenItDoesNotExist() {
        var book = BookFixture.withDefaults();
        var author = AuthorFixture.withDefaults();
        when(authorRepositoryOutboundPort.findById(book.getAuthorId())).thenReturn(Mono.just(author));
        when(bookRepositoryOutboundPort.findById(book.getId())).thenReturn(Mono.empty());
        when(bookRepositoryOutboundPort.save(any(Book.class))).thenReturn(Mono.just(book));

        StepVerifier.create(createBookUseCase.createBook(book))
                .expectNext(book)
                .verifyComplete();

        verify(authorRepositoryOutboundPort).findById(book.getAuthorId());
        verify(bookRepositoryOutboundPort).findById(book.getId());
        verify(bookRepositoryOutboundPort).save(book);
    }

    @Test
    void shouldThrowExceptionWhenBookAlreadyExists() {
        var book = BookFixture.withDefaults();
        var author = AuthorFixture.withDefaults();
        when(authorRepositoryOutboundPort.findById(book.getAuthorId())).thenReturn(Mono.just(author));
        when(bookRepositoryOutboundPort.findById(book.getId())).thenReturn(Mono.just(book));

        StepVerifier.create(createBookUseCase.createBook(book))
                .expectError(BookAlreadyExistsException.class)
                .verify();

        verify(authorRepositoryOutboundPort).findById(book.getAuthorId());
        verify(bookRepositoryOutboundPort).findById(book.getId());
    }

    @Test
    void shouldThrowExceptionWhenAuthorDoesNotExist() {
        var book = BookFixture.withDefaults();
        when(authorRepositoryOutboundPort.findById(book.getAuthorId())).thenReturn(Mono.empty());

        StepVerifier.create(createBookUseCase.createBook(book))
                .expectError(AuthorNotFoundException.class)
                .verify();

        verify(authorRepositoryOutboundPort).findById(book.getAuthorId());
    }
}
