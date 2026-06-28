package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.in.author.GetAuthorByIdInboundPort;
import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class GetAuthorByIdUseCase implements GetAuthorByIdInboundPort {
    private final AuthorRepositoryOutboundPort authorRepository;

    public GetAuthorByIdUseCase(AuthorRepositoryOutboundPort authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Mono<Author> getAuthorById(UUID id) {
        return authorRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new AuthorNotFoundException(id)));
    }
}
