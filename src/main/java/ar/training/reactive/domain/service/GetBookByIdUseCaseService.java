package ar.training.reactive.domain.service;

import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.port.BookRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GetBookByIdUseCaseService {
    private final BookRepositoryPort bookRepository;
    public GetBookByIdUseCaseService(BookRepositoryPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Book> getBookById(UUID id) {
        return bookRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)));
    }
}
