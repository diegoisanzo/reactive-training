package ar.training.reactive.domain.port.out;

import ar.training.reactive.domain.model.Book;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BookRepositoryOutputPort {
    Mono<Book> findById(UUID id);
    Flux<Book> findAll();
    Mono<Book> save(Book book);
    Mono<Void> delete(Book book);
}
