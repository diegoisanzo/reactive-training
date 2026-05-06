package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.CreateBookDtoFixture;
import ar.training.reactive.fixture.BookFixture;
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

import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class CreateBookApplicationTest {

    private final WebTestClient webTestClient;
    private final R2dbcBookRepository bookRepository;
    private final R2dbcEntityTemplate template;

    @Autowired
    CreateBookApplicationTest(
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
    void shouldReturn400WhenCreatingBookWithNullIsbn() {
        var invalidDto = new CreateBookDto(null, "Valid Title");
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    var detail = result.getResponseBody();
                    assertEquals(400, detail.getStatus());
                });
    }

    @Test
    void shouldReturn400WhenCreatingBookWithEmptyIsbn() {
        var invalidDto = new CreateBookDto("", "Valid Title");
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithIsbnExceedingMaxLength() {
        var invalidDto = new CreateBookDto("12345678901234", "Valid Title");
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithNullTitle() {
        var invalidDto = new CreateBookDto("9780000000000", null);
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithEmptyTitle() {
        var invalidDto = new CreateBookDto("9780000000000", "");
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithTitleExceedingMaxLength() {
        var longTitle = "a".repeat(256);
        var invalidDto = new CreateBookDto("9780000000000", longTitle);
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }
}
