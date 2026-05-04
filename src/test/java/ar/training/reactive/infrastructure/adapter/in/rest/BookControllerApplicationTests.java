package ar.training.reactive.infrastructure.adapter.in.rest;


import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.BookDtoFixture;
import ar.training.reactive.fixture.BookFixture;
import ar.training.reactive.fixture.CreateBookDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BookDto;
import ar.training.reactive.infrastructure.adapter.out.persistence.R2dbcBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_BY_ID_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class BookControllerApplicationTests {

    private final WebTestClient webTestClient;
    private final R2dbcBookRepository bookRepository;
    private final R2dbcEntityTemplate template;

    @Autowired
    BookControllerApplicationTests(
            WebTestClient webTestClient,
            R2dbcBookRepository bookRepository,
            R2dbcEntityTemplate template) {
        this.webTestClient = webTestClient;
        this.bookRepository = bookRepository;
        this.template = template;
    }

    @BeforeEach
    void beforeEach() {
        bookRepository.deleteAll()
                .thenMany(Flux.fromIterable(BookFixture.all()).flatMap(template::insert))
                .blockLast();
    }

    @Test
    void shouldCreateBook() {
        var createBookDto = CreateBookDtoFixture.withDefaults();
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(createBookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .value(newBookDto -> {
                    assertNotNull(newBookDto);
                    assertNotNull(newBookDto.id());
                    assertEquals(createBookDto.isbn(), newBookDto.isbn());
                    assertEquals(createBookDto.title(), newBookDto.title());
                });
    }

    @Test
    void shouldReturnAllBooks() {
        webTestClient.get()
                .uri(BOOK_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(3)
                .contains(BookDtoFixture.withDefaults());
    }

    @Test
    void shouldReturnBookById() {
        var expected = BookDtoFixture.withDefaults();
        webTestClient.get()
                .uri(BOOK_BY_ID_PATH, expected.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(expected);
    }

    @Test
    void shouldReturn404WhenBookNotFoundById() throws URISyntaxException {
        var id = new UUID(0, 0);
        var expectedProblemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, "Book with id %s not found".formatted(id));
        expectedProblemDetail.setTitle("Book not found");
        var bookById = new URI(BOOK_BY_ID_PATH
            .replace("{id}", id.toString()));
        expectedProblemDetail.setInstance(bookById);

        webTestClient.get()
                .uri(BOOK_BY_ID_PATH, id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .isEqualTo(expectedProblemDetail);
    }

    @Test
    void shouldUpdateBookViaHttp() {
        var updated = BookDtoFixture.withUpdatesToDefault();
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(updated);
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentBook() {
        var nonExistent = new BookDto(UUID.randomUUID(), "9780000000000", "Unknown");
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(nonExistent)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteBook() {
        var id = BookDtoFixture.withDefaults().id();
        webTestClient.delete()
                .uri(BOOK_BY_ID_PATH, id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentBook() {
        webTestClient.delete()
                .uri(BOOK_BY_ID_PATH, UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }
}
