package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.fixture.BookFixture;
import ar.training.reactive.infrastructure.adapter.out.persistence.R2dbcBookRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class TestDataSetup {
    private final R2dbcBookRepository bookRepository;
    private final R2dbcEntityTemplate template;

    public TestDataSetup(R2dbcBookRepository bookRepository, R2dbcEntityTemplate template) {
        this.bookRepository = bookRepository;
        this.template = template;
    }

    public void refresh() {
        bookRepository.deleteAll()
            .thenMany(Flux.fromIterable(BookFixture.all()).flatMap(template::insert))
            .blockLast();
    }
}
