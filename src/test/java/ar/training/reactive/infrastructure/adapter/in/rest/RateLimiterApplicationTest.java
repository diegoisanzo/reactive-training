package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.author.CreateAuthorDtoFixture;
import ar.training.reactive.fixture.book.BookDtoFixture;
import ar.training.reactive.fixture.book.CreateBookDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorDto;
import ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData;
import ar.training.reactive.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.CREATE_AUTHOR_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.CREATE_BOOK_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.DELETE_AUTHOR_BY_ID_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.DELETE_BOOK_BY_ID_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.GET_ALL_AUTHORS_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.GET_ALL_BOOKS_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.GET_AUTHOR_BY_ID_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.GET_BOOK_BY_ID_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.LIMIT_FOR_PERIOD;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.LIMIT_REFRESH_PERIOD;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.UPDATE_AUTHOR_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.RateLimiterApplicationTest.TestProperties.UPDATE_BOOK_RATE_LIMIT;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_BY_ID_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.book.BookController.BOOK_BY_ID_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.book.BookController.BOOK_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        CREATE_BOOK_RATE_LIMIT + LIMIT_FOR_PERIOD,
        CREATE_BOOK_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        GET_ALL_BOOKS_RATE_LIMIT + LIMIT_FOR_PERIOD,
        GET_ALL_BOOKS_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        GET_BOOK_BY_ID_RATE_LIMIT + LIMIT_FOR_PERIOD,
        GET_BOOK_BY_ID_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        UPDATE_BOOK_RATE_LIMIT + LIMIT_FOR_PERIOD,
        UPDATE_BOOK_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        DELETE_BOOK_BY_ID_RATE_LIMIT + LIMIT_FOR_PERIOD,
        DELETE_BOOK_BY_ID_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        CREATE_AUTHOR_RATE_LIMIT + LIMIT_FOR_PERIOD,
        CREATE_AUTHOR_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        GET_ALL_AUTHORS_RATE_LIMIT + LIMIT_FOR_PERIOD,
        GET_ALL_AUTHORS_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        GET_AUTHOR_BY_ID_RATE_LIMIT + LIMIT_FOR_PERIOD,
        GET_AUTHOR_BY_ID_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        UPDATE_AUTHOR_RATE_LIMIT + LIMIT_FOR_PERIOD,
        UPDATE_AUTHOR_RATE_LIMIT + LIMIT_REFRESH_PERIOD,

        DELETE_AUTHOR_BY_ID_RATE_LIMIT + LIMIT_FOR_PERIOD,
        DELETE_AUTHOR_BY_ID_RATE_LIMIT + LIMIT_REFRESH_PERIOD,
})
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class RateLimiterApplicationTest extends BaseApplicationTest {

    interface TestProperties {
        String RESILIENCE4J_RATELIMITER_INSTANCES = "resilience4j.ratelimiter.instances.";

        String CREATE_BOOK_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "createBookRateLimit.";
        String GET_ALL_BOOKS_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "getAllBooksRateLimit.";
        String GET_BOOK_BY_ID_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "getBookByIdRateLimit.";
        String UPDATE_BOOK_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "updateBookRateLimit.";
        String DELETE_BOOK_BY_ID_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "deleteBookByIdRateLimit.";

        String CREATE_AUTHOR_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "createAuthorRateLimit.";
        String GET_ALL_AUTHORS_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "getAllAuthorsRateLimit.";
        String GET_AUTHOR_BY_ID_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "getAuthorByIdRateLimit.";
        String UPDATE_AUTHOR_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "updateAuthorRateLimit.";
        String DELETE_AUTHOR_BY_ID_RATE_LIMIT = RESILIENCE4J_RATELIMITER_INSTANCES + "deleteAuthorByIdRateLimit.";

        String LIMIT_FOR_PERIOD = "limitForPeriod=1";
        String LIMIT_REFRESH_PERIOD = "limitRefreshPeriod=10s";
    }

    @Autowired
    RateLimiterApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldReturn429ForGetAllBooksWhenRateLimitExceeded() {
        var request = authedGet(BOOK_PATH);
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForGetBookByIdWhenRateLimitExceeded() {
        var id = BookDtoFixture.withDefaults().id();
        var request = authedGet(BOOK_BY_ID_PATH, id);
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForCreateBookWhenRateLimitExceeded() {
        var request = authedPost(BOOK_PATH).bodyValue(CreateBookDtoFixture.withDefaults());
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForUpdateBookWhenRateLimitExceeded() {
        var request = authedPut(BOOK_PATH).bodyValue(BookDtoFixture.withUpdatesToDefault());
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForDeleteBookWhenRateLimitExceeded() {
        var id = BookDtoFixture.withDefaults().id();
        var request = authedAdminDelete(BOOK_BY_ID_PATH, id);
        request.exchange().expectStatus().isNoContent();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForGetAllAuthorsWhenRateLimitExceeded() {
        var request = authedGet(AUTHOR_PATH);
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForGetAuthorByIdWhenRateLimitExceeded() {
        var id = AuthorDBData.ROBERT_MARTIN.getId();
        var request = authedGet(AUTHOR_BY_ID_PATH, id);
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForCreateAuthorWhenRateLimitExceeded() {
        var request = authedPost(AUTHOR_PATH).bodyValue(CreateAuthorDtoFixture.withDefaults());
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForUpdateAuthorWhenRateLimitExceeded() {
        var updated = new AuthorDto(AuthorDBData.ROBERT_MARTIN.getId(), "Updated Name");
        var request = authedPut(AUTHOR_PATH).bodyValue(updated);
        request.exchange().expectStatus().isOk();
        assertIsStatus429AndRateLimitExceeded(request.exchange());
    }

    @Test
    void shouldReturn429ForDeleteAuthorWhenRateLimitExceeded() {
        var request = authedAdminDelete(AUTHOR_BY_ID_PATH, UUID.randomUUID());
        request.exchange().expectStatus().isNotFound();
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
