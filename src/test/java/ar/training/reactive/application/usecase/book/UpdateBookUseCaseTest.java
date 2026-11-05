package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import ar.training.reactive.domain.exception.book.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.fixture.author.AuthorFixture;
import ar.training.reactive.fixture.book.BookFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBookUseCaseTest {

    @Mock
    private BookRepositoryOutboundPort bookRepositoryOutboundPort;

    @Mock
    private AuthorRepositoryOutboundPort authorRepositoryOutboundPort;

    @InjectMocks
    private UpdateBookUseCase updateBookUseCase;

    @Test
    void shouldUpdateBookWhenExistsAndDataChanged() {
        var existingBook = BookFixture.withDefaults();
        var incomingBook = new Book(existingBook.getId(), "9781234567890", "New Title", 15, Genre.NON_FICTION, existingBook.getAuthorId());
        
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
        verifyNoInteractions(authorRepositoryOutboundPort);
    }

    @Test
    void shouldNotSaveWhenDataHasNotChanged() {
        var existingBook = BookFixture.withDefaults();
        var incomingBook = new Book(existingBook.getId(), existingBook.getIsbn(), existingBook.getTitle(), existingBook.getAvailableCopies(), existingBook.getGenre(), existingBook.getAuthorId());
        
        when(bookRepositoryOutboundPort.findById(existingBook.getId())).thenReturn(Mono.just(existingBook));

        StepVerifier.create(updateBookUseCase.updateBook(incomingBook))
                .expectNext(existingBook)
                .verifyComplete();

        verify(bookRepositoryOutboundPort).findById(existingBook.getId());
        verify(bookRepositoryOutboundPort, never()).save(any(Book.class));
        verifyNoInteractions(authorRepositoryOutboundPort);
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

    @Test
    void shouldUpdateAuthorIdWhenAuthorExists() {
        var existingBook = BookFixture.withDefaults();
        var newAuthor = AuthorFixture.withDefaults();
        var newAuthorId = newAuthor.getId();
        assertNotEquals(existingBook.getAuthorId(), newAuthorId);
        var incomingBook = new Book(
                existingBook.getId(),
                existingBook.getIsbn(),
                existingBook.getTitle(),
                existingBook.getAvailableCopies(),
                existingBook.getGenre(),
                newAuthorId);

        when(bookRepositoryOutboundPort.findById(existingBook.getId())).thenReturn(Mono.just(existingBook));
        when(authorRepositoryOutboundPort.findById(newAuthorId)).thenReturn(Mono.just(newAuthor));
        when(bookRepositoryOutboundPort.save(any(Book.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(updateBookUseCase.updateBook(incomingBook))
                .expectNextMatches(updated -> updated.getAuthorId().equals(newAuthorId))
                .verifyComplete();

        verify(authorRepositoryOutboundPort).findById(newAuthorId);
        verify(bookRepositoryOutboundPort).save(any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenNewAuthorDoesNotExist() {
        var existingBook = BookFixture.withDefaults();
        var newAuthorId = UUID.randomUUID();
        var incomingBook = new Book(
                existingBook.getId(),
                existingBook.getIsbn(),
                existingBook.getTitle(),
                existingBook.getAvailableCopies(),
                existingBook.getGenre(),
                newAuthorId);

        when(bookRepositoryOutboundPort.findById(existingBook.getId())).thenReturn(Mono.just(existingBook));
        when(authorRepositoryOutboundPort.findById(newAuthorId)).thenReturn(Mono.empty());

        StepVerifier.create(updateBookUseCase.updateBook(incomingBook))
                .expectError(AuthorNotFoundException.class)
                .verify();

        verify(bookRepositoryOutboundPort, never()).save(any(Book.class));
    }
}
