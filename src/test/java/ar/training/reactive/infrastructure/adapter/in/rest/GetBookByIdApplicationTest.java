package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.GetBookByIdInboundPort;
import ar.training.reactive.fixture.BookDtoFixture;
import ar.training.reactive.fixture.BookFixture;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_BY_ID_PATH;
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
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetBookByIdApplicationTest {

    private final WebTestClient webTestClient;
    private final TestDataSetup testDataSetup;
    @MockitoSpyBean
    private GetBookByIdInboundPort getBookByIdInboundPort;

    @Autowired
    GetBookByIdApplicationTest(
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
        var bookById = new URI(BOOK_BY_ID_PATH.replace("{id}", id.toString()));
        expectedProblemDetail.setInstance(bookById);

        webTestClient.get()
                .uri(BOOK_BY_ID_PATH, id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .isEqualTo(expectedProblemDetail);
    }

    @Test
    void shouldFailToGetBookByIdWhenExceedingTimeLimit() {
        var id = BookDtoFixture.withDefaults().id();
        doAnswer(_ -> Mono
                .delay(Duration.of(1200, MILLIS))
                .then(Mono.just(BookFixture.withDefaults()))
        )
            .when(getBookByIdInboundPort)
            .getBookById(any(UUID.class));
        webTestClient.get()
                .uri(BOOK_BY_ID_PATH, id)
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
                    assertEquals("/v1/books/" + id, properties.get("path"));
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
