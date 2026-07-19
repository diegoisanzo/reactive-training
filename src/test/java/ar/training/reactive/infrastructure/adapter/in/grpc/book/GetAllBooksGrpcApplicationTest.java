package ar.training.reactive.infrastructure.adapter.in.grpc.book;

import ar.training.reactive.BookGrpcServiceGrpc;
import ar.training.reactive.BookProto;
import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.book.GetAllBooksInboundPort;
import ar.training.reactive.fixture.book.BookFixture;
import ar.training.reactive.infrastructure.adapter.in.grpc.BaseGrpcApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.security.jwt.JwtService;
import com.google.protobuf.Empty;
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
class GetAllBooksGrpcApplicationTest extends BaseGrpcApplicationTest {

    private final BookGrpcServiceGrpc.BookGrpcServiceBlockingStub bookStub;

    @MockitoSpyBean
    private GetAllBooksInboundPort getAllBooksInboundPort;

    @Autowired
    GetAllBooksGrpcApplicationTest(GrpcChannelFactory channelFactory, TestDataSetup testDataSetup, JwtService jwtService) {
        super(channelFactory, testDataSetup, jwtService);
        this.bookStub = BookGrpcServiceGrpc.newBlockingStub(channel);
    }

    @Test
    void shouldReturnAllBooksWithReadRole() {
        var bookListProto = authedReadUserStub(bookStub).getAllBooks(Empty.getDefaultInstance());
        assertEquals(3, bookListProto.getBooksCount());
        assertTrue(bookListProto.getBooksList().contains(expectedBookProto()));
    }

    @Test
    void shouldReturnAllBooksWithReadWriteRole() {
        var bookListProto = authedReadWriteUserStub(bookStub).getAllBooks(Empty.getDefaultInstance());
        assertEquals(3, bookListProto.getBooksCount());
        assertTrue(bookListProto.getBooksList().contains(expectedBookProto()));
    }

    @Test
    void shouldReturnAllBooksWithAdminRole() {
        var bookListProto = authedAdminUserStub(bookStub).getAllBooks(Empty.getDefaultInstance());
        assertEquals(3, bookListProto.getBooksCount());
        assertTrue(bookListProto.getBooksList().contains(expectedBookProto()));
    }

    @Test
    void shouldFailUnauthenticatedWithoutToken() {
        var ex = assertThrows(StatusRuntimeException.class,
                () -> bookStub.getAllBooks(Empty.getDefaultInstance()));

        assertUnauthenticated(ex);
    }

    @Test
    void shouldFailUnauthenticatedWithInvalidToken() {
        var ex = assertThrows(StatusRuntimeException.class,
                () -> invalidTokenStub(bookStub).getAllBooks(Empty.getDefaultInstance()));

        assertUnauthenticated(ex);
    }

    @Test
    void shouldFailPermissionDeniedWithoutReadRole() {
        var ex = assertThrows(StatusRuntimeException.class,
                () -> authedWriteOnlyUserStub(bookStub).getAllBooks(Empty.getDefaultInstance()));

        assertPermissionDenied(ex);
    }

    @Test
    void shouldFailPermissionDeniedWithAdminOnlyRole() {
        var ex = assertThrows(StatusRuntimeException.class,
                () -> authedAdminOnlyUserStub(bookStub).getAllBooks(Empty.getDefaultInstance()));

        assertPermissionDenied(ex);
    }

    @Test
    void shouldFailUnknownWhenUseCaseFails() {
        doReturn(fluxExplodes())
                .when(getAllBooksInboundPort).getAllBooks();

        var ex = assertThrows(StatusRuntimeException.class,
                () -> authedReadUserStub(bookStub).getAllBooks(Empty.getDefaultInstance()));

        assertUnknown(ex);
    }

    private BookProto expectedBookProto() {
        var book = BookFixture.withDefaults();
        return BookProto.newBuilder()
                .setId(book.getId().toString())
                .setIsbn(book.getIsbn())
                .setTitle(book.getTitle())
                .setAvailableCopies(book.getAvailableCopies())
                .setGenre(book.getGenre().name())
                .setAuthorId(book.getAuthorId().toString())
                .build();
    }
}
