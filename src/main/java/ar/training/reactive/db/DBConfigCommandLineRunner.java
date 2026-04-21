package ar.training.reactive.db;

import ar.training.reactive.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
public class DBConfigCommandLineRunner {

    private final Logger log;
    private final DatabaseClient client;

    @Autowired
    public DBConfigCommandLineRunner(DatabaseClient client) {
        this.log = LoggerFactory.getLogger(getClass());
        this.client = client;
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            createBookTable();
            insertBooks();
        };
    }

    private void insertBooks() {
        BookSeedData.ALL.forEach(this::upsertBook);
    }

    private void upsertBook(Book book) {
        client.sql("""
                        INSERT INTO book (id, isbn, title)
                        VALUES (:id, :isbn, :title)
                        ON CONFLICT (id)
                        DO UPDATE SET
                            isbn = EXCLUDED.isbn,
                            title = EXCLUDED.title;""")
                .bind("id", book.getId())
                .bind("isbn", book.getIsbn())
                .bind("title", book.getTitle())
                .then()
                .doOnSuccess(v -> log.info("'{}' inserted", book.getTitle()))
                .doOnError(e -> log.error("Error inserting book: {}", e.getMessage()))
                .subscribe();
    }

    private void createBookTable() {
        client.sql("""
            CREATE TABLE IF NOT EXISTS book (
                id UUID PRIMARY KEY,
                isbn VARCHAR(13) NOT NULL,
                title VARCHAR(255) NOT NULL
            );
        """)
                .then()
                .doOnSuccess(v -> log.info("Table 'book' verified/created."))
                .doOnError(e -> log.error("Error creating table: {}", e.getMessage()))
                .subscribe();
    }

}
