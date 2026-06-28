package ar.training.reactive.application.port.in.author;

import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Mono;

public interface UpdateAuthorInboundPort {
    Mono<Author> updateAuthor(Author author);
}
