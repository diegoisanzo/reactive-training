package ar.training.reactive.service;

import ar.training.reactive.dto.BookDto;
import ar.training.reactive.entity.Book;
import ar.training.reactive.exception.BookNotFoundException;
import ar.training.reactive.repository.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<BookDto> getBookById(UUID id) {
        return bookRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .map(BookDto::of);
    }

    public Flux<BookDto> getAllBooks() {
        return bookRepository
                .findAll()
                .map(BookDto::of);
    }

    public Mono<BookDto> updateBook(BookDto bookDto) {
        return bookRepository.findById(bookDto.id())
            .switchIfEmpty(Mono.error(new BookNotFoundException(bookDto.id())))
            .flatMap(book -> saveIfUpdated(book, bookDto))
            .map(BookDto::of);
    }

    private Mono<Book> saveIfUpdated(Book book, BookDto bookDto) {
        if (book.updateFrom(bookDto)) {
            return bookRepository.save(book);
        }
        return Mono.just(book);
    }

    public Mono<Void> deleteById(UUID id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .flatMap(bookRepository::delete);
    }
}
