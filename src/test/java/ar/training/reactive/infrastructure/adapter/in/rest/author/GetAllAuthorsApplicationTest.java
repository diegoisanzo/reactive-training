package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.author.CreateAuthorDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorApplicationTestHelper.createAuthor;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetAllAuthorsApplicationTest extends BaseApplicationTest {

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
}
