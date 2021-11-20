package ru.nik.commons.http.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.errors.Function2;
import ru.nik.commons.http.errors.Function3;
import ru.nik.commons.webflux.HttpErrorHandler;

import java.util.Map;
import java.util.Optional;

public interface HttpExchangeConfiguration {

    String getBaseUrl();

    MeterRegistry getMeterRegistry();

    Timer getTimer();

    CircuitBreaker getCircuitBreaker();

    Map<String, String> getStandardHeaders();

    HttpErrorHandler getHttpErrorHandler();

    Function3<ClientResponse, Optional<String>, String, Mono<String>> getErrorResponseMapper();

    Function2<ClientResponse, Optional<String>, Mono<Void>> getOkResponseValidator();

    String getLoggingName();
}
