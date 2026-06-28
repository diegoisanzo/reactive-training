package ar.training.reactive.application.port.in.author;

import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetAuthorByIdInboundPort {
    Mono<Author> getAuthorById(UUID id);
}
