package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.domain.model.Author;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData;
import ar.training.reactive.infrastructure.adapter.out.persistence.book.BookDBData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class DBConfigCommandLineRunner {

    private final Logger log;
    private final DatabaseClient client;

    public DBConfigCommandLineRunner(DatabaseClient client) {
        this.log = LoggerFactory.getLogger(getClass());
        this.client = client;
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            try {
                createAuthorTable()
                        .then(Mono.defer(this::insertAuthors))
                        .then(Mono.defer(this::createBookTable))
                        .then(Mono.defer(this::insertBooks))
                        .block();
                log.info("Database initialization completed successfully.");
            } catch (Exception e) {
                log.error("Database initialization failed.", e);
                throw e;
            }
        };
    }

    private Mono<Void> insertAuthors() {
        return Flux.fromIterable(AuthorDBData.ALL)
                .flatMap(this::upsertAuthor)
                .then();
    }

    private Mono<Void> upsertAuthor(Author author) {
        return client.sql("""
                        INSERT INTO author (id, name)
                        VALUES (:id, :name)
                        ON CONFLICT (id)
                        DO UPDATE SET
                            name = EXCLUDED.name;""")
                .bind("id", author.getId())
                .bind("name", author.getName())
                .then()
                .doOnSuccess(v -> log.info("'{}' inserted", author.getName()))
                .doOnError(e -> log.error("Error inserting author", e));
    }

    private Mono<Void> insertBooks() {
        return Flux.fromIterable(BookDBData.ALL)
                .flatMap(this::upsertBook)
                .then();
    }

    private Mono<Void> upsertBook(Book book) {
        return client.sql("""
                        INSERT INTO book (id, isbn, title, available_copies, genre, author_id)
                        VALUES (:id, :isbn, :title, :availableCopies, :genre, :authorId)
                        ON CONFLICT (id)
                        DO UPDATE SET
                            isbn = EXCLUDED.isbn,
                            title = EXCLUDED.title,
                            available_copies = EXCLUDED.available_copies,
                            genre = EXCLUDED.genre,
                            author_id = EXCLUDED.author_id;""")
                .bind("id", book.getId())
                .bind("isbn", book.getIsbn())
                .bind("title", book.getTitle())
                .bind("availableCopies", book.getAvailableCopies())
                .bind("genre", book.getGenre().name())
                .bind("authorId", book.getAuthorId())
                .then()
                .doOnSuccess(v -> log.info("'{}' inserted", book.getTitle()))
                .doOnError(e -> log.error("Error inserting book", e));
    }

    private Mono<Void> createBookTable() {
        return client.sql("""
            CREATE TABLE IF NOT EXISTS book (
                id UUID PRIMARY KEY,
                isbn VARCHAR(13) NOT NULL,
                title VARCHAR(255) NOT NULL,
                available_copies BIGINT NOT NULL DEFAULT 0,
                genre VARCHAR(50) NOT NULL,
                author_id UUID NOT NULL REFERENCES author(id)
            );
        """)
                .then()
                .doOnSuccess(v -> log.info("Table 'book' verified/created."))
                .doOnError(e -> log.error("Error creating table", e));
    }

    private Mono<Void> createAuthorTable() {
        return client.sql("""
            CREATE TABLE IF NOT EXISTS author (
                id UUID PRIMARY KEY,
                name VARCHAR(255) NOT NULL
            );
        """)
                .then()
                .doOnSuccess(v -> log.info("Table 'author' verified/created."))
                .doOnError(e -> log.error("Error creating table", e));
    }
}
