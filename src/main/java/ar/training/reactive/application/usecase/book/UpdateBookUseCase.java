package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.in.book.UpdateBookInboundPort;
import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.book.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

public class UpdateBookUseCase implements UpdateBookInboundPort {

    private final BookRepositoryOutboundPort bookRepository;

    public UpdateBookUseCase(BookRepositoryOutboundPort bookRepository) {
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
