package ar.training.reactive.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.UUID;

@Configuration
public class DBConfigCommandLineRunner {

    private final DatabaseClient client;

    @Autowired
    public DBConfigCommandLineRunner(DatabaseClient client) {
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
        upsertBook(
                UUID.fromString("93d68169-2722-427f-8556-0762db939eb7"),
                "9780132350884",
                "Clean Code");
        upsertBook(
                UUID.fromString("93d68169-2722-427f-8556-0762db939eb8"),
                "9780132350885",
                "Design Patterns");
        upsertBook(
                UUID.fromString("93d68169-2722-427f-8556-0762db939eb9"),
                "9780132350886",
                "Database Management Systems");
    }

    private void upsertBook(UUID id, String isbn, String title) {
        client.sql("""
                        INSERT INTO book (id, isbn, title)
                        VALUES (:id, :isbn, :title)
                        ON CONFLICT (id)
                        DO UPDATE SET
                            isbn = EXCLUDED.isbn,
                            title = EXCLUDED.title;""")
                .bind("id", id)
                .bind("isbn", isbn)
                .bind("title", title)
                .then()
                .doOnSuccess(v -> System.out.println("'" + title + "' inserted*"))
                .doOnError(e -> System.err.println("Error inserting book: " + e.getMessage()))
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
                .doOnSuccess(v -> System.out.println("Tabla 'book' verificada/creada."))
                .doOnError(e -> System.err.println("Error creando la tabla: " + e.getMessage()))
                .subscribe(); // Importante: En flujos reactivos, si no te suscribes, no pasa nada.
    }

}
