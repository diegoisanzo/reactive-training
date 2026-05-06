package ar.training.reactive.application.usecase;

import ar.training.reactive.application.port.in.GetBookByIdInboundPort;
import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GetBookByIdUseCase implements GetBookByIdInboundPort {
    private final BookRepositoryOutboundPort bookRepository;
    public GetBookByIdUseCase(BookRepositoryOutboundPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Book> getBookById(UUID id) {
        return bookRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)));
    }
}
