package ar.training.reactive;

import ar.training.reactive.controller.BookController;
import ar.training.reactive.dto.BookDto;
import ar.training.reactive.fixture.BookDtoFixture;
import ar.training.reactive.fixture.BookFixture;
import ar.training.reactive.repository.BookRepository;
import ar.training.reactive.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.postgresql.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers
@AutoConfigureWebTestClient
class ReactiveTrainingApplicationTests {

    @ServiceConnection
    private static final PostgreSQLContainer postgresSQLContainer =
            new PostgreSQLContainer("postgres:16-alpine");

    private final WebTestClient webTestClient;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final ApplicationContext applicationContext;
    private final R2dbcEntityTemplate template;

    @Autowired
    ReactiveTrainingApplicationTests(
            WebTestClient webTestClient,
            BookService bookService,
            BookRepository bookRepository,
            ApplicationContext applicationContext,
            R2dbcEntityTemplate template) {
        this.webTestClient = webTestClient;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.applicationContext = applicationContext;
        this.template = template;
    }

    @BeforeEach
    void beforeEach() {
        bookRepository.deleteAll()
                .thenMany(Flux.fromIterable(BookFixture.all()).flatMap(template::insert))
                .blockLast();
    }

    @Test
    void contextLoads() {
        assertContainsBeanOfType(BookController.class);
        assertContainsBeanOfType(BookService.class);
        assertContainsBeanOfType(BookRepository.class);
    }

    private void assertContainsBeanOfType(final Class<?> requiredType) {
        var bean = applicationContext.getBean(requiredType);
        assertThat(bean)
                .isNotNull()
                .isInstanceOf(requiredType);
    }

    @Test
    void shouldUpdateBook() {
        StepVerifier
                .create(bookService.updateBook(BookDtoFixture.withUpdatesToDefault()))
                .expectNext(BookDtoFixture.withUpdatesToDefault())
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
    void shouldReturn404WhenBookNotFoundById() {
        webTestClient.get()
                .uri("/v1/books/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
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
