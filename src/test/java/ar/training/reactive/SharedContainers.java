package ar.training.reactive;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;

public class SharedContainers {
    @ServiceConnection
    static final PostgreSQLContainer postgresSQLContainer =
            new PostgreSQLContainer("postgres:17-alpine");
}
