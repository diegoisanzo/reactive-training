package ar.training.reactive.application.port.out.author;

import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AuthorRepositoryOutboundPort {
    Mono<Author> findById(UUID id);
    Flux<Author> findAll();
    Mono<Author> save(Author author);
    Mono<Void> delete(Author author);
}
