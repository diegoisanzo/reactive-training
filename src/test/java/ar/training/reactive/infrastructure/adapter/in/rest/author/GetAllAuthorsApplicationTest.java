package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.author.GetAllAuthorsInboundPort;
import ar.training.reactive.domain.model.Author;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorApplicationTestHelper.createAuthor;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetAllAuthorsApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private GetAllAuthorsInboundPort getAllAuthorsInboundPort;

    @Autowired
    GetAllAuthorsApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldReturnAllAuthors() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());

        authedReadUserClient().get()
                .uri(AUTHOR_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .contains(created);
    }

    @Test
    void shouldFailToGetAllAuthorsWhenExceedingTimeLimit() {
        doAnswer(_ -> Mono.delay(Duration.of(3200, MILLIS)).thenMany(Flux.<Author>empty()))
            .when(getAllAuthorsInboundPort)
            .getAllAuthors();
        authedReadUserClient().get()
                .uri(AUTHOR_PATH)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, AUTHOR_PATH));
    }
}
