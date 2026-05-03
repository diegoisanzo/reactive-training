package ar.training.reactive.domain.service;

import ar.training.reactive.domain.exception.BookAlreadyExistsException;
import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.port.BookRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateBookUseCaseService {

    private final BookRepositoryPort bookRepository;

    public CreateBookUseCaseService(BookRepositoryPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Book> createBook(Book book) {
        return bookRepository.findById(book.getId())
                .flatMap(transformer -> Mono.error(new BookAlreadyExistsException(book.getId())))
                .then(bookRepository.save(book));

    }

}
