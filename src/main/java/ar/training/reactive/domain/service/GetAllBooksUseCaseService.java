package ar.training.reactive.domain.service;

import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.port.out.BookRepositoryOutputPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetAllBooksUseCaseService {
    private final BookRepositoryOutputPort bookRepository;

    public GetAllBooksUseCaseService(BookRepositoryOutputPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
