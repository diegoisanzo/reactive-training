package ar.training.reactive.application.port.in.book;

import ar.training.reactive.domain.model.Book;
import reactor.core.publisher.Mono;

public interface CreateBookInboundPort {
    Mono<Book> createBook(Book book);
}
