package ar.training.reactive.application.port.in.book;

import ar.training.reactive.domain.model.Book;
import reactor.core.publisher.Flux;

public interface GetAllBooksInboundPort {
    Flux<Book> getAllBooks();
}
