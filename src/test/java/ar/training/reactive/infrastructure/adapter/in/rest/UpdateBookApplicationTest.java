package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.BookDtoFixture;
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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class UpdateBookApplicationTest {

    private final WebTestClient webTestClient;
    private final R2dbcBookRepository bookRepository;
    private final R2dbcEntityTemplate template;

    @Autowired
    UpdateBookApplicationTest(
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
    void shouldReturn400WhenUpdatingBookWithNullId() {
        var invalidDto = new BookDto(null, "9780000000000", "Valid Title");
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullIsbn() {
        var invalidDto = new BookDto(UUID.randomUUID(), null, "Valid Title");
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithEmptyIsbn() {
        var invalidDto = new BookDto(UUID.randomUUID(), "", "Valid Title");
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithIsbnExceedingMaxLength() {
        var invalidDto = new BookDto(UUID.randomUUID(), "12345678901234", "Valid Title");
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullTitle() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", null);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithEmptyTitle() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", "");
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithTitleExceedingMaxLength() {
        var longTitle = "a".repeat(256);
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", longTitle);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }
}
