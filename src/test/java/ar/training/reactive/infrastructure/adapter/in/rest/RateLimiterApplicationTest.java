package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.BookDtoFixture;
import ar.training.reactive.fixture.CreateBookDtoFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_BY_ID_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "resilience4j.ratelimiter.instances.createBookRateLimit.limitForPeriod=1",
        "resilience4j.ratelimiter.instances.getAllBooksRateLimit.limitForPeriod=1",
        "resilience4j.ratelimiter.instances.getBookByIdRateLimit.limitForPeriod=1",
        "resilience4j.ratelimiter.instances.updateBookRateLimit.limitForPeriod=1",
        "resilience4j.ratelimiter.instances.deleteBookByIdRateLimit.limitForPeriod=1",
})
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class RateLimiterApplicationTest {

    private final WebTestClient webTestClient;
    private final TestDataSetup testDataSetup;

    @Autowired
    RateLimiterApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup) {
        this.webTestClient = webTestClient;
        this.testDataSetup = testDataSetup;
    }

    @BeforeEach
    void beforeEach() {
        testDataSetup.refresh();
    }

    @Test
    void shouldReturn429ForGetAllBooksWhenRateLimitExceeded() {
        var getAllBooksRequest = webTestClient.get().uri(BOOK_PATH);
        getAllBooksRequest.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(getAllBooksRequest.exchange());
    }

    @Test
    void shouldReturn429ForGetBookByIdWhenRateLimitExceeded() {
        var id = BookDtoFixture.withDefaults().id();
        var getBookByIdRequest = webTestClient.get().uri(BOOK_BY_ID_PATH, id);
        getBookByIdRequest.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(getBookByIdRequest.exchange());
    }

    @Test
    void shouldReturn429ForCreateBookWhenRateLimitExceeded() {
        var createBookDto = CreateBookDtoFixture.withDefaults();
        var createBookRequest = webTestClient.post().uri(BOOK_PATH).bodyValue(createBookDto);
        createBookRequest.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(createBookRequest.exchange());
    }

    @Test
    void shouldReturn429ForUpdateBookWhenRateLimitExceeded() {
        var bookDto = BookDtoFixture.withUpdatesToDefault();
        var updateBookRequest = webTestClient.put().uri(BOOK_PATH).bodyValue(bookDto);
        updateBookRequest.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(updateBookRequest.exchange());
    }

    @Test
    void shouldReturn429ForDeleteBookWhenRateLimitExceeded() {
        var id = BookDtoFixture.withDefaults().id();
        var deleteBookByIdRequest = webTestClient.delete().uri(BOOK_BY_ID_PATH, id);

        deleteBookByIdRequest.exchange().expectStatus().isNoContent();

        assertIsStatus429AndRateLimitExceeded(deleteBookByIdRequest.exchange());
    }

    private void assertIsStatus429AndRateLimitExceeded(WebTestClient.ResponseSpec response) {
        response
                .expectStatus().isEqualTo(429)
                .expectBody(ProblemDetail.class)
                .value(this::assertRateLimitExceeded);
    }

    private void assertRateLimitExceeded(ProblemDetail pd) {
        assertNotNull(pd);
        assertEquals("Rate limit exceeded", pd.getTitle());
        assertEquals(429, pd.getStatus());
        assertNull(pd.getDetail());
    }
}
