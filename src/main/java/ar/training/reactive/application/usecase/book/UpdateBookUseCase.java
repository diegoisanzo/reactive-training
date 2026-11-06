package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.in.book.UpdateBookInboundPort;
import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import ar.training.reactive.domain.exception.book.BookNotFoundException;
import ar.training.reactive.domain.model.Book;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class UpdateBookUseCase implements UpdateBookInboundPort {

    private final BookRepositoryOutboundPort bookRepository;
    private final AuthorRepositoryOutboundPort authorRepository;

    public UpdateBookUseCase(BookRepositoryOutboundPort bookRepository, AuthorRepositoryOutboundPort authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public Mono<Book> updateBook(Book book) {
        return bookRepository.findById(book.getId())
                .switchIfEmpty(Mono.error(new BookNotFoundException(book.getId())))
                .flatMap(existing -> ensureAuthorExistsIfChanged(existing, book)
                        .then(Mono.defer(() -> saveIfUpdated(existing, book))));
    }

    private Mono<Void> ensureAuthorExistsIfChanged(Book existing, Book incoming) {
        if (Objects.equals(existing.getAuthorId(), incoming.getAuthorId())) {
            return Mono.empty();
        }
        return authorRepository.findById(incoming.getAuthorId())
                .switchIfEmpty(Mono.error(new AuthorNotFoundException(incoming.getAuthorId())))
                .then();
    }

    private Mono<Book> saveIfUpdated(Book existing, Book incoming) {
        if (existing.updateFrom(incoming)) {
            return bookRepository.save(existing);
        }
        return Mono.just(existing);
    }
}
