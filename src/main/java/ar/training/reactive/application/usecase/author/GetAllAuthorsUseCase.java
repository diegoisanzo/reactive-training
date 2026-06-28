package ar.training.reactive.application.usecase.author;

import ar.training.reactive.application.port.in.author.GetAllAuthorsInboundPort;
import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.model.Author;
import reactor.core.publisher.Flux;

public class GetAllAuthorsUseCase implements GetAllAuthorsInboundPort {
    private final AuthorRepositoryOutboundPort authorRepository;

    public GetAllAuthorsUseCase(AuthorRepositoryOutboundPort authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Flux<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
}
