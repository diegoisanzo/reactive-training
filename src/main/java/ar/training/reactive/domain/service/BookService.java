package ar.training.reactive.domain.service;

import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.port.BookRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class BookService {

    private final BookRepositoryPort bookRepository;

    public BookService(BookRepositoryPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Book> getBookById(UUID id) {
        return bookRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)));
    }

    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
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

    public Mono<Void> deleteById(UUID id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .flatMap(bookRepository::delete);
    }
}
