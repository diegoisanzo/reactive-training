package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.infrastructure.security.jwt.JwtService;
import ar.training.reactive.infrastructure.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseApplicationTest {

    protected final WebTestClient webTestClient;
    protected final TestDataSetup testDataSetup;
    protected final JwtService jwtService;

    public BaseApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        this.webTestClient = webTestClient;
        this.testDataSetup = testDataSetup;
        this.jwtService = jwtService;
    }

    @BeforeEach
    void setUp() {
        testDataSetup.refresh();
    }

    protected WebTestClient authedReadUserClient() {
        return authedClient("read-user", List.of(Role.READ.name()));
    }

    protected WebTestClient authedReadWriteUserClient() {
        return authedClient("read-write-user", List.of(Role.READ.name(), Role.WRITE.name()));
    }

    protected WebTestClient authedAdminUserClient() {
        return authedClient("admin-user", Role.ALL_NAMES);
    }

    private WebTestClient authedClient(String username, List<String> roles) {
        return webTestClient.mutate()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " +
                        jwtService.generateToken(username, roles))
                .build();
    }

    protected WebTestClient.RequestBodySpec authedPost(String path) {
        return authedReadWriteUserClient().post().uri(path);
    }

    protected WebTestClient.RequestBodySpec authedPost(String path, Object... uriVariables) {
        return authedReadWriteUserClient().post().uri(path, uriVariables);
    }

    protected WebTestClient.RequestBodySpec authedPut(String path) {
        return authedReadWriteUserClient().put().uri(path);
    }

    protected WebTestClient.RequestBodySpec authedPut(String path, Object... uriVariables) {
        return authedReadWriteUserClient().put().uri(path, uriVariables);
    }

    protected WebTestClient.RequestHeadersSpec<?> authedGet(String path) {
        return authedReadUserClient().get().uri(path);
    }

    protected WebTestClient.RequestHeadersSpec<?> authedGet(String path, Object... uriVariables) {
        return authedReadUserClient().get().uri(path, uriVariables);
    }

    protected WebTestClient.RequestHeadersSpec<?> authedAdminDelete(String path) {
        return authedAdminUserClient().delete().uri(path);
    }

    protected WebTestClient.RequestHeadersSpec<?> authedAdminDelete(String path, Object... uriVariables) {
        return authedAdminUserClient().delete().uri(path, uriVariables);
    }

    protected void assertBadRequestOnPost(String path, Object invalidRequestBody) {
        assertBadRequestOnPost(path, invalidRequestBody, Map.of());
    }

    protected void assertBadRequestOnPost(String path, Object invalidRequestBody, String expectedField, String expectedMessage) {
        assertBadRequestOnPost(path, invalidRequestBody, Map.of(expectedField, expectedMessage));
    }

    protected void assertBadRequestOnPost(String path, Object invalidRequestBody, Map<String, String> expectedFieldErrors) {
        assertBadRequest(authedPost(path).bodyValue(invalidRequestBody), path, expectedFieldErrors);
    }

    protected void assertBadRequestOnPut(String path, Object invalidRequestBody) {
        assertBadRequestOnPut(path, invalidRequestBody, Map.of());
    }

    protected void assertBadRequestOnPut(String path, Object invalidRequestBody, String expectedField, String expectedMessage) {
        assertBadRequestOnPut(path, invalidRequestBody, Map.of(expectedField, expectedMessage));
    }

    protected void assertBadRequestOnPut(String path, Object invalidRequestBody, Map<String, String> expectedFieldErrors) {
        assertBadRequest(authedPut(path).bodyValue(invalidRequestBody), path, expectedFieldErrors);
    }

    protected void assertBadRequest(WebTestClient.RequestHeadersSpec<?> request, String path, String expectedField, String expectedMessage) {
        assertBadRequest(request, path, Map.of(expectedField, expectedMessage));
    }

    protected void assertBadRequest(WebTestClient.RequestHeadersSpec<?> request, String path, Map<String, String> expectedFieldErrors) {
        request.exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertNotNull(problemDetail);
                    assertEquals(400, problemDetail.getStatus());
                    assertEquals("Validation Failed", problemDetail.getTitle());
                    assertEquals("Input validation failed", problemDetail.getDetail());
                    assertEquals(URI.create(path), problemDetail.getInstance());

                    var properties = problemDetail.getProperties();
                    assertNotNull(properties, "ProblemDetail properties should not be null");
                    @SuppressWarnings("unchecked")
                    var fieldErrors = (List<Map<String, Object>>) properties.get("fieldErrors");
                    assertNotNull(fieldErrors, "fieldErrors should not be null");

                    expectedFieldErrors.forEach((field, message) -> {
                        boolean match = fieldErrors.stream().anyMatch(error ->
                                field.equals(error.get("field")) &&
                                message.equals(error.get("message"))
                        );
                        assertTrue(match, () -> String.format(
                                "Expected fieldError with field='%s' and message='%s', but got: %s",
                                field, message, fieldErrors
                        ));
                    });
                });
    }

    protected static void assertUnauthorized(ProblemDetail problemDetail, String path) {
        assertProblemDetail(problemDetail, 401, "Unauthorized", path);
    }

    protected static void assertInternalServerError(ProblemDetail problemDetail, String path) {
        assertProblemDetail(problemDetail, 500, "Internal Server Error", path);
    }

    protected static void assertProblemDetail(ProblemDetail problemDetail, int status, String error, String path) {
        assertNotNull(problemDetail);
        assertNull(problemDetail.getType());
        assertEquals(error, problemDetail.getTitle());
        assertEquals(status, problemDetail.getStatus());
        assertNull(problemDetail.getDetail());
        assertNull(problemDetail.getInstance());
        var properties = problemDetail.getProperties();
        assertNotNull(properties);
        assertEquals(path, properties.get("path"));
        assertEquals(error, properties.get("error"));
        assertNotNull(properties.get("timestamp"));
        assertTrue(properties.containsKey("requestId"), "Should contain requestId key");
        var requestIdValue = properties.get("requestId");
        assertNotNull(requestIdValue, "requestId value should not be null");
        assertInstanceOf(String.class, requestIdValue, "requestId should be a String");
        assertFalse(((String) requestIdValue).isBlank(), "requestId should not be blank");
    }
}
