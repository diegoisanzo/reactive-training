package ar.training.reactive.domain.service;

import ar.training.reactive.domain.exception.BookAlreadyExistsException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.port.out.BookRepositoryOutputPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateBookUseCaseService {

    private final BookRepositoryOutputPort bookRepositoryOutputPort;

    public CreateBookUseCaseService(BookRepositoryOutputPort bookRepositoryOutputPort) {
        this.bookRepositoryOutputPort = bookRepositoryOutputPort;
    }

    public Mono<Book> createBook(Book book) {
        return bookRepositoryOutputPort.findById(book.getId())
                .flatMap(transformer -> Mono.error(new BookAlreadyExistsException(book.getId())))
                .then(bookRepositoryOutputPort.save(book));

    }

}
