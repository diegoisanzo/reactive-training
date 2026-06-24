package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.in.book.CreateBookInboundPort;
import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.book.BookAlreadyExistsException;
import ar.training.reactive.domain.model.Book;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

public class CreateBookUseCase implements CreateBookInboundPort {

    private final BookRepositoryOutboundPort bookRepository;

    public CreateBookUseCase(BookRepositoryOutboundPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Book> createBook(Book book) {
        return bookRepository.findById(book.getId())
                .flatMap(transformer -> Mono.error(new BookAlreadyExistsException(book.getId())))
                .then(bookRepository.save(book));

    }

}
