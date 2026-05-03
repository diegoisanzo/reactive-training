package ar.training.reactive.application;

import ar.training.reactive.adapter.inbound.rest.BookDto;
import ar.training.reactive.adapter.outbound.persistence.R2dbcBookRepository;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.service.UpdateBookUseCaseService;
import ar.training.reactive.fixture.BookDtoFixture;
import ar.training.reactive.fixture.BookFixture;
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
import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class BookApplicationTests {

    private final WebTestClient webTestClient;
    private final UpdateBookUseCaseService updateBookUseCaseService;
    private final R2dbcBookRepository bookRepository;
    private final R2dbcEntityTemplate template;

    @Autowired
    BookApplicationTests(
            WebTestClient webTestClient,
            UpdateBookUseCaseService updateBookUseCaseService,
            R2dbcBookRepository bookRepository,
            R2dbcEntityTemplate template) {
        this.webTestClient = webTestClient;
        this.updateBookUseCaseService = updateBookUseCaseService;
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
    void shouldUpdateBook() {
        var updated = BookDtoFixture.withUpdatesToDefault();
        var book = new Book(updated.id(), updated.isbn(), updated.title());
        StepVerifier
                .create(updateBookUseCaseService.updateBook(book))
                .expectNextMatches(b ->
                        b.getId().equals(updated.id()) &&
                        b.getIsbn().equals(updated.isbn()) &&
                        b.getTitle().equals(updated.title()))
                .verifyComplete();
    }

    @Test
    void shouldReturnAllBooks() {
        webTestClient.get()
                .uri("/v1/books")
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
                .uri("/v1/books/{id}", expected.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(expected);
    }

    @Test
    void shouldReturn404WhenBookNotFoundById() throws URISyntaxException {
        var id = UUID.randomUUID();
        var expectedProblemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, "Book with id %s not found".formatted(id));
        expectedProblemDetail.setTitle("Book not found");
        expectedProblemDetail.setInstance(new URI("/v1/books/%s".formatted(id)));

        webTestClient.get()
                .uri("/v1/books/{id}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .isEqualTo(expectedProblemDetail);
    }

    @Test
    void shouldUpdateBookViaHttp() {
        var updated = BookDtoFixture.withUpdatesToDefault();
        webTestClient.put()
                .uri("/v1/books")
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
                .uri("/v1/books")
                .bodyValue(nonExistent)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteBook() {
        var id = BookDtoFixture.withDefaults().id();
        webTestClient.delete()
                .uri("/v1/books/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentBook() {
        webTestClient.delete()
                .uri("/v1/books/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }
}
