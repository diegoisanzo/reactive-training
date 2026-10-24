package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.author.CreateAuthorInboundPort;
import ar.training.reactive.domain.model.Author;
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

import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_BY_ID_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class CreateAuthorApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private CreateAuthorInboundPort createAuthorInboundPort;

    @Autowired
    CreateAuthorApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldCreateAuthor() {
        var createAuthorDto = CreateAuthorDtoFixture.withDefaults();
        authedReadWriteUserClient().post()
                .uri(AUTHOR_PATH)
                .bodyValue(createAuthorDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .value(newAuthorDto -> {
                    assertNotNull(newAuthorDto);
                    assertNotNull(newAuthorDto.id());
                    assertEquals(createAuthorDto.name(), newAuthorDto.name());
                });
    }

    @Test
    void shouldPersistCreatedAuthorWhenFetchedAgain() {
        var createAuthorDto = CreateAuthorDtoFixture.withDefaults();
        var created = authedReadWriteUserClient().post()
                .uri(AUTHOR_PATH)
                .bodyValue(createAuthorDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .returnResult()
                .getResponseBody();

        authedReadUserClient().get()
                .uri(AUTHOR_BY_ID_PATH, created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .isEqualTo(created);
    }

    @Test
    void shouldReturn400WhenCreatingAuthorWithNullName() {
        assertBadRequestOnPost(AUTHOR_PATH, new CreateAuthorDto(null));
    }

    @Test
    void shouldReturn400WhenCreatingAuthorWithEmptyName() {
        assertBadRequestOnPost(AUTHOR_PATH, new CreateAuthorDto(""));
    }

    @Test
    void shouldReturn400WhenCreatingAuthorWithBlankName() {
        assertBadRequestOnPost(AUTHOR_PATH, new CreateAuthorDto("   "));
    }

    @Test
    void shouldReturn400WhenCreatingAuthorWithNameExceedingMaxLength() {
        var longName = "a".repeat(Author.NAME_MAX_LENGTH + 1);
        assertBadRequestOnPost(AUTHOR_PATH, new CreateAuthorDto(longName));
    }

    @Test
    void shouldCreateAuthorWithNameOfExactlyMaxLength() {
        var maxLengthName = "a".repeat(Author.NAME_MAX_LENGTH);
        authedReadWriteUserClient().post()
                .uri(AUTHOR_PATH)
                .bodyValue(new CreateAuthorDto(maxLengthName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .value(created -> assertEquals(maxLengthName, created.name()));
    }

    @Test
    void shouldFailToCreateAuthorWhenExceedingTimeLimit() {
        doAnswer(_ -> Mono.delay(Duration.of(2200, MILLIS)).then(Mono.just(AuthorFixture.withDefaults())))
            .when(createAuthorInboundPort)
            .createAuthor(any(Author.class));
        authedReadWriteUserClient().post()
                .uri(AUTHOR_PATH)
                .bodyValue(CreateAuthorDtoFixture.withDefaults())
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, AUTHOR_PATH));
    }
}
