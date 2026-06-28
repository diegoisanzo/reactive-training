package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.in.author.CreateAuthorInboundPort;
import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorAlreadyExistsException;
import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Mono;

public class CreateAuthorUseCase implements CreateAuthorInboundPort {

    private final AuthorRepositoryOutboundPort authorRepository;

    public CreateAuthorUseCase(AuthorRepositoryOutboundPort authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Mono<Author> createAuthor(Author author) {
        return authorRepository.findById(author.getId())
                .flatMap(transformer -> Mono.error(new AuthorAlreadyExistsException(author.getId())))
                .then(authorRepository.save(author));
    }
}
