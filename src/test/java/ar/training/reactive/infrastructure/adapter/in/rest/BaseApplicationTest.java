package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.infrastructure.security.JwtService;
import ar.training.reactive.infrastructure.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

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
