package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.in.book.CreateBookInboundPort;
import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import ar.training.reactive.domain.exception.book.BookAlreadyExistsException;
import ar.training.reactive.domain.model.Book;
import reactor.core.publisher.Mono;

public class CreateBookUseCase implements CreateBookInboundPort {

    private final BookRepositoryOutboundPort bookRepository;
    private final AuthorRepositoryOutboundPort authorRepository;

    public CreateBookUseCase(BookRepositoryOutboundPort bookRepository, AuthorRepositoryOutboundPort authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public Mono<Book> createBook(Book book) {
        return authorRepository.findById(book.getAuthorId())
                .switchIfEmpty(Mono.error(new AuthorNotFoundException(book.getAuthorId())))
                .then(Mono.defer(() -> bookRepository.findById(book.getId())))
                .flatMap(existing -> Mono.error(new BookAlreadyExistsException(book.getId())))
                .then(Mono.defer(() -> bookRepository.save(book)));
    }
}
