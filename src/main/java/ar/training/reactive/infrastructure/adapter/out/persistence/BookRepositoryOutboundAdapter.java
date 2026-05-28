package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import ar.training.reactive.domain.model.Book;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class BookRepositoryOutboundAdapter implements BookRepositoryOutboundPort {

    private final R2dbcBookRepository repository;
    private final PersistenceBookMapper persistenceBookMapper;
    private final PersistenceBookEntityMapper persistenceBookEntityMapper;

    public BookRepositoryOutboundAdapter(
            R2dbcBookRepository repository,
            PersistenceBookEntityMapper persistenceBookEntityMapper,
            PersistenceBookMapper persistenceBookMapper) {
        this.repository = repository;
        this.persistenceBookEntityMapper = persistenceBookEntityMapper;
        this.persistenceBookMapper = persistenceBookMapper;
    }

    @Override
    public Mono<Book> findById(UUID id) {
        return repository.findById(id)
                .map(persistenceBookMapper::toBook);
    }

    @Override
    public Flux<Book> findAll() {
        return repository.findAll()
                .map(persistenceBookMapper::toBook);
    }

    @Override
    public Mono<Book> save(Book book) {
        return repository.save(persistenceBookEntityMapper.toBookEntity(book))
                .map(persistenceBookMapper::toBook);
    }

    @Override
    public Mono<Void> delete(Book book) {
        return repository.delete(persistenceBookEntityMapper.toBookEntity(book));
    }
}
