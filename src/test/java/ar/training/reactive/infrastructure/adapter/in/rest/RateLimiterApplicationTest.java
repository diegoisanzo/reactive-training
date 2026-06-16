package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.BookDtoFixture;
import ar.training.reactive.fixture.CreateBookDtoFixture;
import ar.training.reactive.infrastructure.security.JwtService;
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
class RateLimiterApplicationTest extends BaseApplicationTest {

    @Autowired
    RateLimiterApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldReturn429ForGetAllBooksWhenRateLimitExceeded() {
        var request = authedReadUserClient().get().uri(BOOK_PATH);
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForGetBookByIdWhenRateLimitExceeded() {
        var id = BookDtoFixture.withDefaults().id();
        var request = authedReadUserClient().get().uri(BOOK_BY_ID_PATH, id);
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForCreateBookWhenRateLimitExceeded() {
        var request = authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(CreateBookDtoFixture.withDefaults());
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForUpdateBookWhenRateLimitExceeded() {
        var request = authedReadWriteUserClient().put().uri(BOOK_PATH).bodyValue(BookDtoFixture.withUpdatesToDefault());
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForDeleteBookWhenRateLimitExceeded() {
        var id = BookDtoFixture.withDefaults().id();
        var request = authedAdminUserClient().delete().uri(BOOK_BY_ID_PATH, id);
        request.exchange().expectStatus().isNoContent();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    private static void assertIsStatus429AndRateLimitExceeded(WebTestClient.ResponseSpec response) {
        response
                .expectStatus().isEqualTo(429)
                .expectBody(ProblemDetail.class)
                .value(RateLimiterApplicationTest::assertRateLimitExceeded);
    }

    private static void assertRateLimitExceeded(ProblemDetail pd) {
        assertNotNull(pd);
        assertEquals("Rate limit exceeded", pd.getTitle());
        assertEquals(429, pd.getStatus());
        assertNull(pd.getDetail());
    }
}
