package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.UpdateBookInboundPort;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.fixture.BookDtoFixture;
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
import java.util.UUID;

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
class UpdateBookApplicationTest {

    private final WebTestClient webTestClient;
    private final TestDataSetup testDataSetup;
    @MockitoSpyBean
    private UpdateBookInboundPort updateBookInboundPort;

    @Autowired
    UpdateBookApplicationTest(
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
        var nonExistent = new BookDto(UUID.randomUUID(), "9780000000000", "Unknown", 0);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(nonExistent)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullId() {
        var invalidDto = new BookDto(null, "9780000000000", "Valid Title", 0);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullIsbn() {
        var invalidDto = new BookDto(UUID.randomUUID(), null, "Valid Title", 0);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithEmptyIsbn() {
        var invalidDto = new BookDto(UUID.randomUUID(), "", "Valid Title", 0);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithIsbnExceedingMaxLength() {
        var invalidDto = new BookDto(UUID.randomUUID(), "12345678901234", "Valid Title", 0);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullTitle() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", null, 0);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithEmptyTitle() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", "", 0);
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
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", longTitle, 0);
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }

    @Test
    void shouldFailToUpdateBookWhenExceedingTimeLimit() {
        doAnswer(invocation -> Mono
                .delay(Duration.of(2200, MILLIS))
                .then(Mono.just(invocation.getArgument(0)))
        )
            .when(updateBookInboundPort)
            .updateBook(any(Book.class));
        var bookDto = BookDtoFixture.withUpdatesToDefault();
        webTestClient.put()
                .uri(BOOK_PATH)
                .bodyValue(bookDto)
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
                    var properties = problemDetail.getProperties();
                    assertNotNull(properties);
                    assertEquals("/v1/books", properties.get("path"));
                    assertEquals("Internal Server Error", properties.get("error"));
                    assertNotNull(properties.get("timestamp"));
                    assertTrue(properties.containsKey("requestId"), "Should contain requestId key");
                    var requestIdValue = properties.get("requestId");
                    assertNotNull(requestIdValue, "requestId value should not be null");
                    assertInstanceOf(String.class, requestIdValue, "requestId should be a String");
                    assertFalse(((String) requestIdValue).isBlank(), "requestId should not be blank");
                });
    }
}
