package ar.training.reactive.infrastructure.adapter.out.persistence.author;

import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.domain.exception.author.AuthorHasBooksException;
import ar.training.reactive.domain.model.Author;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class AuthorRepositoryOutboundAdapter implements AuthorRepositoryOutboundPort {

    private final R2dbcAuthorRepository repository;
    private final PersistenceAuthorMapper persistenceAuthorMapper;
    private final PersistenceAuthorEntityMapper persistenceAuthorEntityMapper;

    public AuthorRepositoryOutboundAdapter(
            R2dbcAuthorRepository repository,
            PersistenceAuthorEntityMapper persistenceAuthorEntityMapper,
            PersistenceAuthorMapper persistenceAuthorMapper) {
        this.repository = repository;
        this.persistenceAuthorEntityMapper = persistenceAuthorEntityMapper;
        this.persistenceAuthorMapper = persistenceAuthorMapper;
    }

    @Override
    public Mono<Author> findById(UUID id) {
        return repository.findById(id)
                .map(persistenceAuthorMapper::toAuthor);
    }

    @Override
    public Flux<Author> findAll() {
        return repository.findAll()
                .map(persistenceAuthorMapper::toAuthor);
    }

    @Override
    public Mono<Author> save(Author author) {
        var entity = persistenceAuthorEntityMapper.toAuthorEntity(author);
        return repository.existsById(author.getId())
                .doOnNext(exists -> {
                    if (exists) {
                        entity.markAsExisting();
                    }
                })
                .then(Mono.defer(() -> repository.save(entity)))
                .map(persistenceAuthorMapper::toAuthor);
    }

    @Override
    public Mono<Void> delete(Author author) {
        return repository.delete(persistenceAuthorEntityMapper.toAuthorEntity(author))
                .onErrorMap(DataIntegrityViolationException.class, e -> new AuthorHasBooksException(author.getId()));
    }
}
