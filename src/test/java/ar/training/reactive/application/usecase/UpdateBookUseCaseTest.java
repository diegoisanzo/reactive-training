package ar.training.reactive.application.usecase;

import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.fixture.BookFixture;
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
class UpdateBookUseCaseTest {

    @Mock
    private BookRepositoryOutboundPort bookRepositoryOutboundPort;

    @InjectMocks
    private UpdateBookUseCase updateBookUseCase;

    @Test
    void shouldUpdateBookWhenExistsAndDataChanged() {
        var existingBook = BookFixture.withDefaults();
        var incomingBook = new Book(existingBook.getId(), "9781234567890", "New Title");
        
        when(bookRepositoryOutboundPort.findById(existingBook.getId())).thenReturn(Mono.just(existingBook));
        when(bookRepositoryOutboundPort.save(any(Book.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(updateBookUseCase.updateBook(incomingBook))
                .expectNextMatches(updated -> 
                    updated.getId().equals(existingBook.getId()) &&
                    updated.getIsbn().equals("9781234567890") &&
                    updated.getTitle().equals("New Title")
                )
                .verifyComplete();

        verify(bookRepositoryOutboundPort).findById(existingBook.getId());
        verify(bookRepositoryOutboundPort).save(any(Book.class));
    }

    @Test
    void shouldNotSaveWhenDataHasNotChanged() {
        var existingBook = BookFixture.withDefaults();
        var incomingBook = new Book(existingBook.getId(), existingBook.getIsbn(), existingBook.getTitle());
        
        when(bookRepositoryOutboundPort.findById(existingBook.getId())).thenReturn(Mono.just(existingBook));

        StepVerifier.create(updateBookUseCase.updateBook(incomingBook))
                .expectNext(existingBook)
                .verifyComplete();

        verify(bookRepositoryOutboundPort).findById(existingBook.getId());
        verify(bookRepositoryOutboundPort, org.mockito.Mockito.never()).save(any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenBookDoesNotExist() {
        var book = BookFixture.withDefaults();
        when(bookRepositoryOutboundPort.findById(book.getId())).thenReturn(Mono.empty());

        StepVerifier.create(updateBookUseCase.updateBook(book))
                .expectError(BookNotFoundException.class)
                .verify();

        verify(bookRepositoryOutboundPort).findById(book.getId());
    }
}
