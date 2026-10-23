package ar.training.reactive.infrastructure.adapter.in.grpc.book;

import ar.training.reactive.BookGrpcServiceGrpc;
import ar.training.reactive.BookListProto;
import ar.training.reactive.application.port.in.book.GetAllBooksInboundPort;
import ar.training.reactive.infrastructure.adapter.in.grpc.GrpcReactiveStream;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class BookGrpcService extends BookGrpcServiceGrpc.BookGrpcServiceImplBase {

    private final GetAllBooksInboundPort getAllBooksInboundPort;
    private final GrpcBookProtoMapper grpcBookProtoMapper;
    private final Logger logger;

    public BookGrpcService(GetAllBooksInboundPort getAllBooksInboundPort,
                           GrpcBookProtoMapper grpcBookProtoMapper) {
        this.getAllBooksInboundPort = getAllBooksInboundPort;
        this.grpcBookProtoMapper = grpcBookProtoMapper;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public void getAllBooks(Empty empty, StreamObserver<BookListProto> responseObserver) {
        logger.info("BookGrpcService::getAllBooks()");
        GrpcReactiveStream.collect(
                getAllBooksInboundPort.getAllBooks().map(grpcBookProtoMapper::toBookProto),
                responseObserver,
                books -> BookListProto.newBuilder().addAllBooks(books).build()
        );
    }
}
