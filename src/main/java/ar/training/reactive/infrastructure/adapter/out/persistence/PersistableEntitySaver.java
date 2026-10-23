package ar.training.reactive.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public final class PersistableEntitySaver {

    private PersistableEntitySaver() {}

    public static <T extends BaseEntity<ID>, ID, D> Mono<D> save(
            ReactiveCrudRepository<T, ID> repository,
            T entity,
            Function<T, D> toDomain) {
        return repository.existsById(entity.getId())
                .doOnNext(exists -> {
                    if (exists) {
                        entity.markAsExisting();
                    }
                })
                .then(Mono.defer(() -> repository.save(entity)))
                .map(toDomain);
    }
}
