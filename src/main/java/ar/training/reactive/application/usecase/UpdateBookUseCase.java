package ar.training.reactive.application.usecase;

import ar.training.reactive.application.port.in.UpdateBookInboundPort;
import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
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
