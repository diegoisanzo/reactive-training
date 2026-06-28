package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.fixture.book.BookFixture;
import ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData;
import ar.training.reactive.infrastructure.adapter.out.persistence.author.R2dbcAuthorRepository;
import ar.training.reactive.infrastructure.adapter.out.persistence.book.R2dbcBookRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class TestDataSetup {
    private final R2dbcBookRepository bookRepository;
    private final R2dbcAuthorRepository authorRepository;
    private final R2dbcEntityTemplate template;

    public TestDataSetup(R2dbcBookRepository bookRepository, R2dbcAuthorRepository authorRepository, R2dbcEntityTemplate template) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.template = template;
    }

    public void refresh() {
        bookRepository.deleteAll().block();
        authorRepository.deleteAll().block();

        Flux.fromIterable(AuthorDBData.ALL)
            .flatMap(template::insert)
            .blockLast();

        Flux.fromIterable(BookFixture.all())
            .flatMap(template::insert)
            .blockLast();
    }
}
