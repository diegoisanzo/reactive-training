package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.book.BookNotFoundException;
import ar.training.reactive.fixture.book.BookFixture;
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
class GetBookByIdUseCaseTest {

    @Mock
    private BookRepositoryOutboundPort bookRepositoryOutboundPort;

    @InjectMocks
    private GetBookByIdUseCase getBookByIdUseCase;

    @Test
    void shouldReturnBookWhenExists() {
        var book = BookFixture.withDefaults();
        when(bookRepositoryOutboundPort.findById(book.getId())).thenReturn(Mono.just(book));

        StepVerifier.create(getBookByIdUseCase.getBookById(book.getId()))
                .expectNext(book)
                .verifyComplete();

        verify(bookRepositoryOutboundPort).findById(book.getId());
    }

    @Test
    void shouldThrowExceptionWhenBookDoesNotExist() {
        var id = UUID.randomUUID();
        when(bookRepositoryOutboundPort.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(getBookByIdUseCase.getBookById(id))
                .expectError(BookNotFoundException.class)
                .verify();

        verify(bookRepositoryOutboundPort).findById(id);
    }
}
