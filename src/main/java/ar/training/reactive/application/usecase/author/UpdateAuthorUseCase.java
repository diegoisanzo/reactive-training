package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.in.author.UpdateAuthorInboundPort;
import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorNotFoundException;
import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Mono;

public class UpdateAuthorUseCase implements UpdateAuthorInboundPort {

    private final AuthorRepositoryOutboundPort authorRepository;

    public UpdateAuthorUseCase(AuthorRepositoryOutboundPort authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Mono<Author> updateAuthor(Author author) {
        return authorRepository.findById(author.getId())
                .switchIfEmpty(Mono.error(new AuthorNotFoundException(author.getId())))
                .flatMap(existing -> saveIfUpdated(existing, author));
    }

    private Mono<Author> saveIfUpdated(Author existing, Author incoming) {
        if (existing.updateFrom(incoming)) {
            return authorRepository.save(existing);
        }
        return Mono.just(existing);
    }
}
