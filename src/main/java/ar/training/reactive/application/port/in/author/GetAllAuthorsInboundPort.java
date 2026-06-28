package ar.training.reactive.application.port.in.author;

import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Flux;

public interface GetAllAuthorsInboundPort {
    Flux<Author> getAllAuthors();
}
