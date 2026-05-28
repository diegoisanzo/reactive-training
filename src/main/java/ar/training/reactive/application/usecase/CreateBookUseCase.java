package ar.training.reactive.application.usecase;

import ar.training.reactive.application.port.in.CreateBookInboundPort;
import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.BookAlreadyExistsException;
import ar.training.reactive.domain.model.Book;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
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
