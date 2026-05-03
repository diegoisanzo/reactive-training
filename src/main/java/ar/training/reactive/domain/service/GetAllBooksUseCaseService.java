package ar.training.reactive.domain.service;

import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.port.BookRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetAllBooksUseCaseService {
    private final BookRepositoryPort bookRepository;

    public GetAllBooksUseCaseService(BookRepositoryPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
