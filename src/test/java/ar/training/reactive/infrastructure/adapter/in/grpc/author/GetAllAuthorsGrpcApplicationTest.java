package ar.training.reactive.infrastructure.adapter.in.grpc.author;

import ar.training.reactive.AuthorGrpcServiceGrpc;
import ar.training.reactive.AuthorProto;
import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.author.GetAllAuthorsInboundPort;
import ar.training.reactive.infrastructure.adapter.in.grpc.BaseGrpcApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData;
import ar.training.reactive.infrastructure.security.jwt.JwtService;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureTestGrpcTransport
class GetAllAuthorsGrpcApplicationTest extends BaseGrpcApplicationTest {

    private final AuthorGrpcServiceGrpc.AuthorGrpcServiceBlockingStub authorStub;

    @MockitoSpyBean
    private GetAllAuthorsInboundPort getAllAuthorsInboundPort;

    @Autowired
    GetAllAuthorsGrpcApplicationTest(GrpcChannelFactory channelFactory, TestDataSetup testDataSetup, JwtService jwtService) {
        super(channelFactory, testDataSetup, jwtService);
        this.authorStub = AuthorGrpcServiceGrpc.newBlockingStub(channel);
    }

    @Test
    void shouldReturnAllAuthorsWithReadRole() {
        var response = authedReadUserStub(authorStub).getAllAuthors(Empty.getDefaultInstance());
        assertEquals(3, response.getAuthorsCount());
        assertTrue(response.getAuthorsList().contains(expectedAuthorProto()));
    }

    @Test
    void shouldReturnAllAuthorsWithReadWriteRole() {
        var response = authedReadWriteUserStub(authorStub).getAllAuthors(Empty.getDefaultInstance());
        assertEquals(3, response.getAuthorsCount());
        assertTrue(response.getAuthorsList().contains(expectedAuthorProto()));
    }

    @Test
    void shouldReturnAllAuthorsWithAdminRole() {
        var response = authedAdminUserStub(authorStub).getAllAuthors(Empty.getDefaultInstance());
        assertEquals(3, response.getAuthorsCount());
        assertTrue(response.getAuthorsList().contains(expectedAuthorProto()));
    }

    @Test
    void shouldFailUnauthenticatedWithoutToken() {
        var exception = assertThrows(StatusRuntimeException.class,
                () -> authorStub.getAllAuthors(Empty.getDefaultInstance()));

        assertUnauthenticated(exception);
    }

    @Test
    void shouldFailPermissionDeniedWithoutReadRole() {
        var exception = assertThrows(StatusRuntimeException.class,
                () -> authedWriteOnlyUserStub(authorStub).getAllAuthors(Empty.getDefaultInstance()));

        assertPermissionDenied(exception);
    }

    @Test
    void shouldFailPermissionDeniedWithAdminOnlyRole() {
        var exception = assertThrows(StatusRuntimeException.class,
                () -> authedAdminOnlyUserStub(authorStub).getAllAuthors(Empty.getDefaultInstance()));

        assertPermissionDenied(exception);
    }

    @Test
    void shouldFailUnknownWhenUseCaseFails() {
        doReturn(fluxExplodes())
                .when(getAllAuthorsInboundPort).getAllAuthors();

        var exception = assertThrows(StatusRuntimeException.class,
                () -> authedReadUserStub(authorStub).getAllAuthors(Empty.getDefaultInstance()));

        assertUnknown(exception);
    }

    private AuthorProto expectedAuthorProto() {
        var author = AuthorDBData.ROBERT_MARTIN;
        return AuthorProto.newBuilder()
                .setId(author.getId().toString())
                .setName(author.getName())
                .build();
    }
}
