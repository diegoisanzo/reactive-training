package ar.training.reactive.application.port.in.author;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DeleteAuthorByIdInboundPort {
    Mono<Void> deleteAuthorById(UUID id);
}
