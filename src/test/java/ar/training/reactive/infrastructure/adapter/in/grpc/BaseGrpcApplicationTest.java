package ar.training.reactive.infrastructure.adapter.in.grpc;

import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.security.jwt.JwtService;
import ar.training.reactive.infrastructure.security.Role;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseGrpcApplicationTest {

    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of(HttpHeaders.AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER);

    protected final ManagedChannel channel;
    protected final TestDataSetup testDataSetup;
    protected final JwtService jwtService;

    public BaseGrpcApplicationTest(GrpcChannelFactory channelFactory, TestDataSetup testDataSetup, JwtService jwtService) {
        this.channel = channelFactory.createChannel("in-process");
        this.testDataSetup = testDataSetup;
        this.jwtService = jwtService;
    }

    @BeforeEach
    void setUp() {
        testDataSetup.refresh();
    }

    protected <S extends AbstractStub<S>> S authedReadUserStub(S stub) {
        return authedStub(stub, "read-user", List.of(Role.READ.name()));
    }

    protected <S extends AbstractStub<S>> S authedReadWriteUserStub(S stub) {
        return authedStub(stub, "read-write-user", List.of(Role.READ.name(), Role.WRITE.name()));
    }

    protected <S extends AbstractStub<S>> S authedAdminUserStub(S stub) {
        return authedStub(stub, "admin-user", Role.ALL_NAMES);
    }

    protected <S extends AbstractStub<S>> S authedAdminOnlyUserStub(S stub) {
        return authedStub(stub, "admin-only-user", List.of(Role.ADMIN.name()));
    }

    protected <S extends AbstractStub<S>> S authedWriteOnlyUserStub(S stub) {
        return authedStub(stub, "write-only-user", List.of(Role.WRITE.name()));
    }

    protected <S extends AbstractStub<S>> S invalidTokenStub(S stub) {
        return stubWithAuthorization(stub, JwtService.BEARER_PREFIX + "not-a-valid-jwt");
    }

    private <S extends AbstractStub<S>> S authedStub(S stub, String username, List<String> roles) {
        return stubWithAuthorization(stub, JwtService.BEARER_PREFIX + jwtService.generateToken(username, roles));
    }

    private <S extends AbstractStub<S>> S stubWithAuthorization(S stub, String authorizationValue) {
        var metadata = new Metadata();
        metadata.put(AUTHORIZATION_KEY, authorizationValue);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
    }

    protected static void assertStatusCode(Status.Code expected, StatusRuntimeException exception) {
        assertEquals(expected, exception.getStatus().getCode());
    }

    protected void assertUnauthenticated(StatusRuntimeException exception) {
        assertStatusCode(Status.Code.UNAUTHENTICATED, exception);
    }

    protected void assertPermissionDenied(StatusRuntimeException exception) {
        assertStatusCode(Status.Code.PERMISSION_DENIED, exception);
    }

    protected void assertUnknown(StatusRuntimeException exception) {
        assertStatusCode(Status.Code.UNKNOWN, exception);
    }

    protected static <T> Flux<T> fluxExplodes() {
        return Flux.error(new IllegalStateException("Boom!"));
    }
}
