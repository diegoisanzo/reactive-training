package ar.training.reactive.infrastructure.adapter.in.grpc;

import org.mapstruct.Named;

import java.util.UUID;

public interface GrpcUuidMapping {

    String UUID_TO_STRING = "uuidToString";

    @Named(UUID_TO_STRING)
    default String uuidToString(final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }
}
