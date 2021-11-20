package ru.nik.commons.http.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.errors.Function3;
import ru.nik.commons.http.errors.Function2;
import ru.nik.commons.webflux.HttpErrorHandler;
import ru.nik.commons.webflux.handler.NoOpHttpErrorHandler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ParametersHttpExchangeConfiguration implements HttpExchangeConfiguration {

    private final String url;
    private final MeterRegistry meterRegistry;
    private final Timer timer;
    private final Tracer tracer;
    private final CircuitBreaker circuitBreaker;
    private final String loggingName;

    private final Map<String, String> standardHeaders;
    private final HttpErrorHandler httpErrorHandler;

    private final Function3<ClientResponse, Optional<String>, String, Mono<String>> errorResponseMapper;
    private final Function2<ClientResponse, Optional<String>, Mono<Void>> okResponseValidator;

    protected ParametersHttpExchangeConfiguration(ParametersHttpExchangeConfigurationBuilder<?, ?> builder) {
        this.url = builder.url;
        this.meterRegistry = builder.meterRegistry;
        this.timer = Optional.ofNullable(builder.timerName)
                .map(name -> getTimer(builder.meterRegistry, builder.timerName))
                .orElse(null);
        this.tracer = builder.tracer;
        this.circuitBreaker = builder.circuitBreaker;
        this.loggingName = builder.loggingName;

        this.standardHeaders = builder.standardHeaders == null ? new HashMap<>() : builder.standardHeaders;
        this.httpErrorHandler = builder.httpErrorHandler == null ? new NoOpHttpErrorHandler() : builder.httpErrorHandler;

        if (builder.errorResponseMapper == null) {
            this.errorResponseMapper = ParametersHttpExchangeConfiguration::defaultErrorResponseMapper;
        } else {
            this.errorResponseMapper = builder.errorResponseMapper;
        }

        if (builder.okResponseValidator == null) {
            this.okResponseValidator = ParametersHttpExchangeConfiguration::defaultResponseValidator;
        } else {
            this.okResponseValidator = builder.okResponseValidator;
        }

        validate();
    }

    public static Mono<String> defaultErrorResponseMapper(ClientResponse response, Optional<String> body, String path) {
        HttpClientErrorException errorException = new HttpClientErrorException(
                response.statusCode(),
                response.statusCode().getReasonPhrase(),
                body.map(x -> x.getBytes(StandardCharsets.UTF_8))
                        .orElse(null), StandardCharsets.UTF_8
        );
        return Mono.error(errorException);
    }

    public static Mono<Void> defaultResponseValidator(ClientResponse response, Optional<String> body) {
        // Mono должно либо выдать сигнал complete либо error, по умолчанию никакой валидации
        return Mono.empty();
    }

    public static ParametersHttpExchangeConfigurationBuilder<?, ?> builder() {
        return new ParametersHttpExchangeConfigurationBuilderImpl();
    }

    @Override
    public String getBaseUrl() {
        return url;
    }

    public static abstract class ParametersHttpExchangeConfigurationBuilder<C extends ParametersHttpExchangeConfiguration, B extends ParametersHttpExchangeConfigurationBuilder<C, B>> {
        private String url;
        private MeterRegistry meterRegistry;
        private String timerName;
        private Tracer tracer;
        private CircuitBreaker circuitBreaker;
        private String loggingName;
        private Map<String, String> standardHeaders;
        private HttpErrorHandler httpErrorHandler;
        private Function2<ClientResponse, Optional<String>, Mono<Void>> okResponseValidator;
        private Function3<ClientResponse, Optional<String>, String, Mono<String>> errorResponseMapper;

        public B url(String url) {
            this.url = url;
            return self();
        }

        public B meterRegistry(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
            return self();
        }

        public B timerName(String timerName) {
            this.timerName = timerName;
            return self();
        }

        public B tracer(Tracer tracer) {
            this.tracer = tracer;
            return self();
        }

        public B circuitBreaker(CircuitBreaker circuitBreaker) {
            this.circuitBreaker = circuitBreaker;
            return self();
        }

        public B loggingName(String loggingName) {
            this.loggingName = loggingName;
            return self();
        }

        public B standardHeaders(Map<String, String> standardHeaders) {
            this.standardHeaders = standardHeaders;
            return self();
        }

        public B httpErrorHandler(HttpErrorHandler httpErrorHandler) {
            this.httpErrorHandler = httpErrorHandler;
            return self();
        }

        public B errorResponseMapper(Function3<ClientResponse, Optional<String>, String, Mono<String>> errorResponseMapper) {
            this.errorResponseMapper = errorResponseMapper;
            return self();
        }

        public B okResponseValidator(Function2<ClientResponse, Optional<String>, Mono<Void>> okResponseValidator) {
            this.okResponseValidator = okResponseValidator;
            return self();
        }

        protected abstract B self();

        public abstract C build();
    }

    private static final class ParametersHttpExchangeConfigurationBuilderImpl extends ParametersHttpExchangeConfigurationBuilder<ParametersHttpExchangeConfiguration, ParametersHttpExchangeConfigurationBuilderImpl> {
        private ParametersHttpExchangeConfigurationBuilderImpl() {
        }

        protected ParametersHttpExchangeConfigurationBuilderImpl self() {
            return this;
        }

        public ParametersHttpExchangeConfiguration build() {
            return new ParametersHttpExchangeConfiguration(this);
        }
    }

    protected Timer getTimer(MeterRegistry meterRegistry, String name) {
        return Timer.builder(name)
                .sla(
                        Duration.ofMillis(100),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(2),
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(5),
                        Duration.ofSeconds(10),
                        Duration.ofSeconds(20))
                .register(meterRegistry);
    }

    private void validate() {

        Objects.requireNonNull(this.getBaseUrl(), "url is null");
        Objects.requireNonNull(this.getLoggingName(), "loggingName is null");

        if (this.getMeterRegistry() != null)
            Objects.requireNonNull(this.getTimer(), "timer is null");

        if (this.getTimer() != null)
            Objects.requireNonNull(this.getMeterRegistry(), "meterRegistry is null");

        Objects.requireNonNull(this.getStandardHeaders(), "standardHeaders is null");
        Objects.requireNonNull(this.getErrorResponseMapper(), "errorResponseMapper is null");
        Objects.requireNonNull(this.getOkResponseValidator(), "okResponseValidator is null");

    }
}
