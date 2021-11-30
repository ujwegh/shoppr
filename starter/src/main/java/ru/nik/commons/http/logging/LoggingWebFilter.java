package ru.nik.commons.http.logging;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ru.nik.commons.webflux.decorators.HttpLoggingRequestDecorator;
import ru.nik.commons.webflux.decorators.HttpLoggingResponseDecorator;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LoggingWebFilter implements WebFilter {
    public static final String TIMER = "TIMER";
    public static final String TIMER_SAMPLE = "TIMER_SAMPLE";
    public static final String WAS_EXCLUDED = "WAS_EXCLUDED";
    public static final String LOG_HEADERS = "LOG_HEADERS";

    private final MeterRegistry meterRegistry;
    private final Timer timer;

    private boolean logHeaders = false;
    private boolean isGateway = false;
    private Set<String> excludedExact = Collections.emptySet();
    private List<String> excludedStartsWith = Collections.emptyList();

    public LoggingWebFilter(MeterRegistry meterRegistry, boolean isGateway) {
        this(meterRegistry);
        this.isGateway = isGateway;
    }

    public LoggingWebFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.timer = Timer.builder("request_processing_timer")
                .description("Timing of back-end requests processing")
                .sla(intToDuration(List.of(100, 500, 1000, 2000, 3000, 5000, 10000, 20000)))
                .register(meterRegistry);
    }

    Duration[] intToDuration(List<Integer> intervals) {
        return intervals.stream()
                .map(Duration::ofMillis)
                .toArray(Duration[]::new);
    }

    public void setExcludedFromLog(List<String> excludedURIPaths) {
        Assert.notNull(excludedURIPaths, "excludedURIPaths is null");
        this.excludedExact = excludedURIPaths.stream()
                .filter(path -> !path.endsWith("*"))
                .collect(Collectors.toSet());
        this.excludedStartsWith = excludedURIPaths.stream()
                .filter(path -> path.endsWith("*"))
                .map(path -> path.substring(0, path.length() - 1))
                .collect(Collectors.toList());
    }

    public void setLogHeaders(boolean logHeaders) {
        this.logHeaders = logHeaders;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String logPrefix = getLogPrefix(request.getHeaders());

        String uriPath = request.getURI().getPath();
        boolean wasExcluded = wasExcluded(uriPath);
        HttpMethod httpMethod = request.getMethod();

        if (!wasExcluded
                && !isGateway
                && (httpMethod == null || HttpMethod.GET.equals(httpMethod) || HttpMethod.DELETE.equals(httpMethod))) {
            Map<String, String> headers = logHeaders ? LoggingUtils.getSortedHeadersMap(request.getHeaders()) : Collections.emptyMap();
            LoggingUtils.logRequest(log, "", logPrefix, uriPath, httpMethod, headers);
        }

        enrichAttributes(exchange, logPrefix, wasExcluded);

        return chain
                .filter(mutateToLogBodies(exchange, logPrefix, isGateway))
                .doOnCancel(() -> {
                    if (wasExcluded(exchange)) {
                        LoggingUtils.logIgnoredCancel(log, uriPath);
                    } else {
                        long time = exchange.<Timer.Sample>getAttribute(TIMER_SAMPLE).stop(timer);
                        LoggingUtils.logCancel(log, httpMethod, logPrefix, time, uriPath);
                    }
                })
                .contextWrite(context -> context.put(LoggingUtils.LOG_PREFIX, logPrefix));
    }

    private void enrichAttributes(ServerWebExchange exchange, String logPrefix, boolean wasExcluded) {
        if (wasExcluded) {
            exchange.getAttributes().put(WAS_EXCLUDED, wasExcluded);
        } else {
            exchange.getAttributes().put(LoggingUtils.LOG_PREFIX, logPrefix);
            exchange.getAttributes().put(TIMER, timer);
            exchange.getAttributes().put(TIMER_SAMPLE, Timer.start(meterRegistry));
            exchange.getAttributes().put(LOG_HEADERS, logHeaders);
        }
    }

    private String getLogPrefix(HttpHeaders headers) {
        if (headers == null || headers.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        return Optional.ofNullable(headers.get(LoggingUtils.LOG_PREFIX))
                .filter(items -> !items.isEmpty())
                .map(items -> items.get(0))
                .orElse(UUID.randomUUID().toString());
    }

    private boolean wasExcluded(String uriPath) {
        return excludedExact.contains(uriPath)
                || excludedStartsWith.stream().anyMatch(uriPath::startsWith);
    }

    public static boolean wasExcluded(ServerWebExchange exchange) {
        return exchange.<Boolean>getAttributeOrDefault(WAS_EXCLUDED, false);
    }

    private static ServerWebExchange mutateToLogBodies(ServerWebExchange exchange, String logPrefix, boolean isGateway) {
        return mutateToLogBodies(exchange, logPrefix, false, log, isGateway);
    }

    public static ServerWebExchange mutateToLogBodies(ServerWebExchange exchange,
                                               String logPrefix,
                                               boolean responseForErrorHandler,
                                               Logger logger,
                                               boolean isGateway) {
        List<PathContainer.Element> elements = exchange.getRequest().getPath().elements();
        final String uriPath = "/" + elements.stream()
                .filter(element -> element instanceof PathContainer.PathSegment)
                .map(PathContainer.Element::value)
                .collect(Collectors.joining("/"));

        return exchange
                .mutate()
                .request(new HttpLoggingRequestDecorator(exchange, logger, uriPath, logPrefix))
                .response(new HttpLoggingResponseDecorator(exchange, logger, responseForErrorHandler, uriPath, logPrefix, isGateway))
                .build();
    }
}
