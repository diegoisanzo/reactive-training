package ar.training.reactive.application.usecase;

import ar.training.reactive.application.port.in.CreateBookInboundPort;
import ar.training.reactive.domain.exception.BookAlreadyExistsException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateBookUseCase implements CreateBookInboundPort {

    private final BookRepositoryOutboundPort bookRepositoryOutboundPort;

    public CreateBookUseCase(BookRepositoryOutboundPort bookRepositoryOutboundPort) {
        this.bookRepositoryOutboundPort = bookRepositoryOutboundPort;
    }

    public Mono<Book> createBook(Book book) {
        return bookRepositoryOutboundPort.findById(book.getId())
                .flatMap(transformer -> Mono.error(new BookAlreadyExistsException(book.getId())))
                .then(bookRepositoryOutboundPort.save(book));

    }

}
