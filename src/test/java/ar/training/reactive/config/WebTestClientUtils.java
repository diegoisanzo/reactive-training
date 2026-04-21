package ar.training.reactive.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.reactive.server.WebTestClient;

public class WebTestClientUtils {

    private final Logger logger;

    public WebTestClientUtils() {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public WebTestClient webTestClient(int port) {
        logger.info("Creating WebTestClient with port {}", port);
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }
}
