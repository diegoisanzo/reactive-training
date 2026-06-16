package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.infrastructure.security.JwtService;
import ar.training.reactive.infrastructure.security.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static ar.training.reactive.infrastructure.adapter.in.rest.AuthController.LOGIN_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class AuthApplicationTest extends BaseApplicationTest {

    @Autowired
    AuthApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldLoginAndReturnValidTokenForReadUser() {
        assertSuccessfulLogin("read-user", List.of(Role.READ.name()));
    }

    @Test
    void shouldLoginAndReturnValidTokenForReadWriteUser() {
        assertSuccessfulLogin("read-write-user", List.of(Role.READ.name(), Role.WRITE.name()));
    }

    @Test
    void shouldLoginAndReturnValidTokenForAdminUser() {
        assertSuccessfulLogin("admin-user", Role.ALL_NAMES);
    }

    @Test
    void shouldReturn401WhenPasswordIsInvalid() {
        var loginDto = new LoginDto("admin-user", "wrong-password");
        webTestClient.post()
                .uri(LOGIN_PATH)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ProblemDetail.class)
                .value(AuthApplicationTest::assertUnauthorizedProblemDetail);
    }

    private static void assertUnauthorizedProblemDetail(ProblemDetail pd) {
        assertProblemDetail(pd, 401, "Unauthorized", LOGIN_PATH);
    }

    @Test
    void shouldReturn401WhenUserDoesNotExist() {
        var loginDto = new LoginDto("unknown-user", "password");
        webTestClient.post()
                .uri(LOGIN_PATH)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ProblemDetail.class)
                .value(AuthApplicationTest::assertUnauthorizedProblemDetail);
    }

    @Test
    void shouldReturn400WhenUsernameIsBlank() {
        var loginDto = new LoginDto("", "password");
        webTestClient.post()
                .uri(LOGIN_PATH)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(AuthApplicationTest::assertBadRequestProblemDetail);
    }

    @Test
    void shouldReturn400WhenPasswordIsBlank() {
        var loginDto = new LoginDto("admin-user", "");
        webTestClient.post()
                .uri(LOGIN_PATH)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(AuthApplicationTest::assertBadRequestProblemDetail);
    }

    private static void assertBadRequestProblemDetail(ProblemDetail pd) {
        assertNotNull(pd);
        assertEquals(400, pd.getStatus());
        assertEquals("Validation Failed", pd.getTitle());
        assertEquals("Input validation failed", pd.getDetail());
    }

    private void assertSuccessfulLogin(String username, List<String> expectedRoles) {
        var loginDto = new LoginDto(username, "password");
        var tokenDto = webTestClient.post()
                .uri(LOGIN_PATH)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(tokenDto);
        assertFalse(tokenDto.token().isBlank());
        assertEquals(username, jwtService.extractUsername(tokenDto.token()));
        var actualRoles = jwtService.extractRolesFrom(tokenDto.token());
        assertEquals(expectedRoles.stream().sorted().toList(), actualRoles.stream().sorted().toList());
    }
}
