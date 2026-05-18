package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.GetAllBooksInboundPort;
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
import reactor.core.publisher.Flux;
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
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetAllBooksApplicationTest {

    private final WebTestClient webTestClient;
    private final TestDataSetup testDataSetup;
    @MockitoSpyBean
    private GetAllBooksInboundPort getAllBooksInboundPort;

    @Autowired
    GetAllBooksApplicationTest(
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
    void shouldFailToGetAllBooksWhenExceedingTimeLimit() {
        doAnswer(_ -> Mono.delay(Duration.of(3200, MILLIS))
                .thenMany(Flux.<Book>empty()))
            .when(getAllBooksInboundPort)
            .getAllBooks();
        webTestClient.get()
                .uri(BOOK_PATH)
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
