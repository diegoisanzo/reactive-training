package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.fixture.book.BookFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllBooksUseCaseTest {

    @Mock
    private BookRepositoryOutboundPort bookRepositoryOutboundPort;

    @InjectMocks
    private GetAllBooksUseCase getAllBooksUseCase;

    @Test
    void shouldReturnAllBooks() {
        var books = List.of(BookFixture.withDefaults(), BookFixture.withDefaults());
        when(bookRepositoryOutboundPort.findAll()).thenReturn(Flux.fromIterable(books));

        StepVerifier.create(getAllBooksUseCase.getAllBooks())
                .expectNext(books.getFirst())
                .expectNext(books.getLast())
                .verifyComplete();

        verify(bookRepositoryOutboundPort).findAll();
    }
}
