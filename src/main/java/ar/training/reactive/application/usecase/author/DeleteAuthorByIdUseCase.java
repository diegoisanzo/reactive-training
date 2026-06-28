package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.in.author.DeleteAuthorByIdInboundPort;
import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class DeleteAuthorByIdUseCase implements DeleteAuthorByIdInboundPort {

    private final AuthorRepositoryOutboundPort authorRepository;

    public DeleteAuthorByIdUseCase(AuthorRepositoryOutboundPort authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Mono<Void> deleteAuthorById(UUID id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new AuthorNotFoundException(id)))
                .flatMap(authorRepository::delete);
    }
}
