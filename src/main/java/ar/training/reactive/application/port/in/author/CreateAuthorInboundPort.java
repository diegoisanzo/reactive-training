package ar.training.reactive.application.port.in.author;

import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Mono;

public interface CreateAuthorInboundPort {
    Mono<Author> createAuthor(Author author);
}
