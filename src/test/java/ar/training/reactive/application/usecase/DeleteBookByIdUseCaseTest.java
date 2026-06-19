package ar.training.reactive.application.usecase;

import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.fixture.BookFixture;
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
class DeleteBookByIdUseCaseTest {

    @Mock
    private BookRepositoryOutboundPort bookRepositoryOutboundPort;

    @InjectMocks
    private DeleteBookByIdUseCase deleteBookByIdUseCase;

    @Test
    void shouldDeleteBookWhenExists() {
        var book = BookFixture.withDefaults();
        when(bookRepositoryOutboundPort.findById(book.getId())).thenReturn(Mono.just(book));
        when(bookRepositoryOutboundPort.delete(book)).thenReturn(Mono.empty());

        StepVerifier.create(deleteBookByIdUseCase.deleteBookById(book.getId()))
                .verifyComplete();

        verify(bookRepositoryOutboundPort).findById(book.getId());
        verify(bookRepositoryOutboundPort).delete(book);
    }

    @Test
    void shouldThrowExceptionWhenBookDoesNotExist() {
        var id0 = new UUID(0, 0);

        when(bookRepositoryOutboundPort.findById(id0)).thenReturn(Mono.empty());

        StepVerifier.create(deleteBookByIdUseCase.deleteBookById(id0))
                .expectError(BookNotFoundException.class)
                .verify();

        verify(bookRepositoryOutboundPort).findById(id0);
    }
}
