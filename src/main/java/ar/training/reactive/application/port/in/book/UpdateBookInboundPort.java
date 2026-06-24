package ar.training.reactive.application.port.in.book;

import ar.training.reactive.domain.model.Book;
import reactor.core.publisher.Mono;

public interface UpdateBookInboundPort {
    Mono<Book> updateBook(Book book);
}
