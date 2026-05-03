package ar.training.reactive.domain.service;

import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.port.out.BookRepositoryOutputPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UpdateBookUseCaseService {

    private final BookRepositoryOutputPort bookRepository;

    public UpdateBookUseCaseService(BookRepositoryOutputPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Book> updateBook(Book book) {
        return bookRepository.findById(book.getId())
                .switchIfEmpty(Mono.error(new BookNotFoundException(book.getId())))
                .flatMap(existing -> saveIfUpdated(existing, book));
    }

    private Mono<Book> saveIfUpdated(Book existing, Book incoming) {
        if (existing.updateFrom(incoming)) {
            return bookRepository.save(existing);
        }
        return Mono.just(existing);
    }
}
