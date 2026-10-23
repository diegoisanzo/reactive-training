package ar.training.reactive.infrastructure.adapter.in.grpc;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class GrpcReactiveStream {

    private GrpcReactiveStream() {}

    public static <T, R> void collect(
            Flux<T> flux,
            StreamObserver<R> responseObserver,
            Function<List<T>, R> toResponse) {
        var items = new ArrayList<T>();
        var disposable = flux.subscribe(
                items::add,
                responseObserver::onError,
                () -> {
                    responseObserver.onNext(toResponse.apply(items));
                    responseObserver.onCompleted();
                }
        );
        Context.current().addListener(context -> {
            if (context.isCancelled()) {
                disposable.dispose();
            }
        }, Runnable::run);
    }
}
