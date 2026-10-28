package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.author.GetAuthorByIdInboundPort;
import ar.training.reactive.fixture.author.AuthorFixture;
import ar.training.reactive.fixture.author.CreateAuthorDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.security.jwt.JwtService;
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

import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorApplicationTestHelper.createAuthor;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_BY_ID_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetAuthorByIdApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private GetAuthorByIdInboundPort getAuthorByIdInboundPort;

    @Autowired
    GetAuthorByIdApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldReturnAuthorById() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());

        authedGet(AUTHOR_BY_ID_PATH, created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .isEqualTo(created);
    }

    @Test
    void shouldReturn404WhenAuthorNotFoundById() {
        var id = UUID.randomUUID();

        authedGet(AUTHOR_BY_ID_PATH, id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertEquals("Author not found", problemDetail.getTitle());
                    assertEquals("Author with id " + id + " not found", problemDetail.getDetail());
                });
    }

    @Test
    void shouldFailToGetAuthorByIdWhenExceedingTimeLimit() {
        var id = AuthorFixture.withDefaults().getId();
        doAnswer(_ -> Mono.delay(Duration.of(1200, MILLIS)).then(Mono.just(AuthorFixture.withDefaults())))
            .when(getAuthorByIdInboundPort)
            .getAuthorById(any(UUID.class));
        authedGet(AUTHOR_BY_ID_PATH, id)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, AUTHOR_PATH + "/" + id));
    }
}
