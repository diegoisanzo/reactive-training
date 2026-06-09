package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.CreateBookInboundPort;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.fixture.CreateBookDtoFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class CreateBookApplicationTest {

    private final WebTestClient webTestClient;
    private final TestDataSetup testDataSetup;
    @MockitoSpyBean
    private CreateBookInboundPort createBookInboundPort;

    @Autowired
    CreateBookApplicationTest(
            WebTestClient webTestClient,
            TestDataSetup testDataSetup) {
        this.webTestClient = webTestClient;
        this.testDataSetup = testDataSetup;
    }

    @BeforeEach
    void beforeEach() {
        testDataSetup.refresh();
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
        var invalidDto = new CreateBookDto(null, "Valid Title", 0);
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    var detail = result.getResponseBody();
                    assertNotNull(detail);
                    assertEquals(400, detail.getStatus());
                });
    }

    @Test
    void shouldReturn400WhenCreatingBookWithEmptyIsbn() {
        var invalidDto = new CreateBookDto("", "Valid Title", 0);
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithIsbnExceedingMaxLength() {
        var invalidDto = new CreateBookDto("12345678901234", "Valid Title", 0);
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithNullTitle() {
        var invalidDto = new CreateBookDto("9780000000000", null, 0);
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithEmptyTitle() {
        var invalidDto = new CreateBookDto("9780000000000", "", 0);
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
        var invalidDto = new CreateBookDto("9780000000000", longTitle, 0);
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldFailToCreateBookWhenExceedingTimeLimit() {
        doAnswer(invocation -> Mono
                .delay(Duration.of(2200, MILLIS))
                .then(Mono.just(invocation.getArgument(0)))
        )
            .when(createBookInboundPort)
            .createBook(any(Book.class));
        var createBookDto = CreateBookDtoFixture.withDefaults();
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(createBookDto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertNotNull(problemDetail);

                    assertNull(problemDetail.getType());
                    assertEquals("Internal Server Error", problemDetail.getTitle());
                    assertEquals(500, problemDetail.getStatus());
                    assertNull(problemDetail.getDetail());
                    assertNull(problemDetail.getInstance());

                    // Asserting the properties map
                    var properties = problemDetail.getProperties();

                    assertNotNull(properties);
                    assertEquals("/v1/books", properties.get("path"));
                    assertEquals("Internal Server Error", properties.get("error"));
                    assertNotNull(properties.get("timestamp"));

                    // General assertions for the dynamic requestId
                    assertTrue(properties.containsKey("requestId"), "Should contain requestId key");
                    var requestIdValue = properties.get("requestId");
                    assertNotNull(requestIdValue, "requestId value should not be null");
                    assertInstanceOf(String.class, requestIdValue, "requestId should be a String");
                    assertFalse(((String) requestIdValue).isBlank(), "requestId should not be blank");
                });
    }

    @Test
    void shouldReturn409WhenBookAlreadyExists() {
        var existingBookId = java.util.UUID.randomUUID();
        doAnswer(invocation -> Mono.error(new ar.training.reactive.domain.exception.BookAlreadyExistsException(existingBookId)))
                .when(createBookInboundPort)
                .createBook(any(Book.class));

        var createBookDto = CreateBookDtoFixture.withDefaults();
        webTestClient.post()
                .uri(BOOK_PATH)
                .bodyValue(createBookDto)
                .exchange()
                .expectStatus().isEqualTo(org.springframework.http.HttpStatus.CONFLICT)
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertNotNull(problemDetail);
                    assertEquals("Book already exists", problemDetail.getTitle());
                    assertEquals(409, problemDetail.getStatus());
                    assertEquals("Book with id " + existingBookId + " already exists", problemDetail.getDetail());
                });
    }
}
