package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.book.BookAlreadyExistsException;
import ar.training.reactive.domain.model.Book;
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

    @InjectMocks
    private CreateBookUseCase createBookUseCase;

    @Test
    void shouldCreateBookWhenItDoesNotExist() {
        var book = BookFixture.withDefaults();
        when(bookRepositoryOutboundPort.findById(book.getId())).thenReturn(Mono.empty());
        when(bookRepositoryOutboundPort.save(any(Book.class))).thenReturn(Mono.just(book));

        StepVerifier.create(createBookUseCase.createBook(book))
                .expectNext(book)
                .verifyComplete();

        verify(bookRepositoryOutboundPort).findById(book.getId());
        verify(bookRepositoryOutboundPort).save(book);
    }

    @Test
    void shouldThrowExceptionWhenBookAlreadyExists() {
        var book = BookFixture.withDefaults();
        when(bookRepositoryOutboundPort.findById(book.getId())).thenReturn(Mono.just(book));
        // Mocking save because .then() evaluates its argument during assembly
        when(bookRepositoryOutboundPort.save(any(Book.class))).thenReturn(Mono.empty());

        StepVerifier.create(createBookUseCase.createBook(book))
                .expectError(BookAlreadyExistsException.class)
                .verify();

        verify(bookRepositoryOutboundPort).findById(book.getId());
    }
}
