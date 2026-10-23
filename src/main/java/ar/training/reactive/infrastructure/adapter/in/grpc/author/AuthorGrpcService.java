package ar.training.reactive.infrastructure.adapter.in.grpc.author;

import ar.training.reactive.AuthorGrpcServiceGrpc;
import ar.training.reactive.AuthorListProto;
import ar.training.reactive.application.port.in.author.GetAllAuthorsInboundPort;
import ar.training.reactive.infrastructure.adapter.in.grpc.GrpcReactiveStream;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class AuthorGrpcService extends AuthorGrpcServiceGrpc.AuthorGrpcServiceImplBase {

    private final GetAllAuthorsInboundPort getAllAuthorsInboundPort;
    private final GrpcAuthorProtoMapper grpcAuthorProtoMapper;
    private final Logger logger;

    public AuthorGrpcService(GetAllAuthorsInboundPort getAllAuthorsInboundPort,
                             GrpcAuthorProtoMapper grpcAuthorProtoMapper) {
        this.getAllAuthorsInboundPort = getAllAuthorsInboundPort;
        this.grpcAuthorProtoMapper = grpcAuthorProtoMapper;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public void getAllAuthors(Empty empty, StreamObserver<AuthorListProto> responseObserver) {
        logger.info("AuthorGrpcService::getAllAuthors()");
        GrpcReactiveStream.collect(
                getAllAuthorsInboundPort.getAllAuthors().map(grpcAuthorProtoMapper::toAuthorProto),
                responseObserver,
                authors -> AuthorListProto.newBuilder().addAllAuthors(authors).build()
        );
    }
}
