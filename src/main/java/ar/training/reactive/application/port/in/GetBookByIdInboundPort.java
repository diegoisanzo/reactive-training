package ar.training.reactive.application.port.in;

import ar.training.reactive.domain.model.Book;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetBookByIdInboundPort {
    Mono<Book> getBookById(UUID id);
}
