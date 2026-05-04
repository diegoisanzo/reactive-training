package ar.training.reactive.application.port.in;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DeleteBookByIdInboundPort {
    Mono<Void> deleteById(UUID id);
}
