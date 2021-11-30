package ru.nik.commons.http;

import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;
import ru.nik.commons.http.config.HttpExchangeConfiguration;
import ru.nik.commons.http.errors.Function2;
import ru.nik.commons.http.errors.Function3;
import ru.nik.commons.http.logging.LoggingUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class HttpRequestExecutorImpl implements HttpRequestExecutor {

    private static final String LOG_SUFFIX = "LOG_SUFFIX";

    private static final AtomicInteger atomicInteger = new AtomicInteger(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));

    private final WebClient webClient;
    private final Logger log;
    private final HttpExchangeConfiguration httpExchangeConfiguration;

    public HttpRequestExecutorImpl(WebClient webClient,
                                   Logger log,
                                   HttpExchangeConfiguration httpExchangeConfiguration) {
        this.webClient = webClient;
        this.log = log;
        this.httpExchangeConfiguration = httpExchangeConfiguration;

    }

    @Override
    public WebClient getWebClient() {
        return webClient;
    }

    @Override
    public Mono<String> doGETRequest(String path, Map<String, String> additionalHeaders) {
        return doGETRequest(path, null, additionalHeaders);
    }

    private String getBaseUrl() {
        return httpExchangeConfiguration == null ? "" : httpExchangeConfiguration.getBaseUrl();
    }

    public Mono<String> doGETRequest(String path,
                                     String request,
                                     Map<String, String> additionalHeaders) {

        String baseUrl = getBaseUrl();

        return doRequestWithLog(
                URI.create(baseUrl + path),
                path,
                request,
                request,
                HttpMethod.GET,
                additionalHeaders
        );
    }

    @Override
    public Mono<String> doPOSTRequest(String path,
                                      String request,
                                      Map<String, String> additionalHeaders) {

        String baseUrl = getBaseUrl();

        return doRequestWithLog(
                URI.create(baseUrl + path),
                path,
                request,
                request,
                HttpMethod.POST,
                additionalHeaders
        );
    }

    @Override
    public Mono<String> doPOSTRequest(String path,
                                      byte[] request,
                                      Map<String, String> additionalHeaders) {

        String baseUrl = getBaseUrl();

        return doRequestWithLog(
                URI.create(baseUrl + path),
                path,
                request,
                "bytes",
                HttpMethod.POST,
                additionalHeaders
        );
    }

    @Override
    public Mono<String> doPOSTMultiPartRequest(String path,
                                               MultiValueMap<String, HttpEntity<?>> request,
                                               Map<String, String> additionalHeaders) {

        String baseUrl = getBaseUrl();

        return doRequestWithLog(
                URI.create(baseUrl + path),
                path,
                request,
                null,
                HttpMethod.POST,
                additionalHeaders
        );
    }

    @Override
    public Mono<byte[]> doGETRequestBinary(String path, Map<String, String> additionalHeaders) {

        String baseUrl = getBaseUrl();

        return doRequestWithLogBinary(
                URI.create(baseUrl + path),
                path,
                null,
                null,
                HttpMethod.GET,
                additionalHeaders
        );
    }

    @Override
    public Mono<byte[]> doPOSTRequestBinary(String path,
                                            String request,
                                            Map<String, String> additionalHeaders) {

        String baseUrl = getBaseUrl();

        return doRequestWithLogBinary(
                URI.create(baseUrl + path),
                path,
                request,
                request,
                HttpMethod.POST,
                additionalHeaders
        );
    }

    @Override
    public Mono<String> doPUTRequest(String path,
                                     String request,
                                     Map<String, String> additionalHeaders) {

        String baseUrl = getBaseUrl();

        return doRequestWithLog(
                URI.create(baseUrl + path),
                path,
                request,
                request,
                HttpMethod.PUT,
                additionalHeaders
        );
    }

    @Override
    public Mono<String> doDELETERequest(String path, Map<String, String> additionalHeaders) {
        String baseUrl = getBaseUrl();
        return doRequestWithLog(
                URI.create(baseUrl + path),
                path,
                null,
                null,
                HttpMethod.DELETE,
                additionalHeaders
        );
    }

    @Override
    public Mono<String> doDELETERequest(String path,
                                        String request,
                                        String request4log,
                                        Map<String, String> additionalHeaders) {

        String baseUrl = Optional.ofNullable(httpExchangeConfiguration)
                .map(HttpExchangeConfiguration::getBaseUrl)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("baseUrl is null"));

        return doRequestWithLog(
                URI.create(baseUrl + path),
                path,
                request,
                request4log,
                HttpMethod.DELETE,
                additionalHeaders
        );
    }

    private Mono<byte[]> doRequestWithLogBinary(
            URI uri,
            String path,
            String request,
            String request4log,
            HttpMethod method,
            Map<String, String> additionalHeaders
    ) {
        return doRequestWithLogConfigurable(
                uri,
                path,
                request,
                request4log,
                method,
                additionalHeaders,
                byte[].class,
                binary -> "*** binary body ***",
                (clientResponse, ts) -> Mono.empty(),
                new byte[]{0},
                (clientResponse, ts, ur) -> Mono.error(new RuntimeException())
        );
    }

    private Mono<String> doRequestWithLog(
            URI uri,
            String path,
            Object request,
            String request4log,
            HttpMethod method,
            Map<String, String> additionalHeaders
    ) {
        return doRequestWithLogConfigurable(
                uri,
                path,
                request,
                request4log,
                method,
                additionalHeaders,
                String.class,
                Function.identity(),
                httpExchangeConfiguration.getOkResponseValidator(),
                "",
                httpExchangeConfiguration.getErrorResponseMapper()
        );
    }

    protected <T> Mono<T> doRequestWithLogConfigurable(URI uri,
                                                       String path,
                                                       Object request,
                                                       String request4log,
                                                       HttpMethod method,
                                                       Map<String, String> additionalHeaders,
                                                       Class<T> responseClass,
                                                       Function<T, String> bodyToLoggableString,
                                                       Function2<ClientResponse, Optional<T>, Mono<Void>> okResponseValidator,
                                                       T returnIfResponseIsEmpty,
                                                       Function3<ClientResponse, Optional<T>, String, Mono<T>> errorResponseMapper) {
        final AtomicReference<Timer.Sample> timerSample = new AtomicReference<>();

        final Mono<ClientResponse> httpExchange = Mono.just(getRequestSpec(uri, method, request, additionalHeaders))
                .flatMap(requestSpec -> Mono.deferContextual(Mono::just)
                        .flatMap(context -> requestSpec.exchange()
                                .doOnRequest(n -> {
                                    logRequest(context, path, method, request4log, requestSpec);
                                    if (httpExchangeConfiguration.getMeterRegistry() != null) {
                                        timerSample.set(Timer.start(httpExchangeConfiguration.getMeterRegistry()));
                                    }
                                })
                        )
                );

        return httpExchange
                .transform(httpExchangeConfiguration.getCircuitBreaker() == null
                        ? Function.identity()
                        : CircuitBreakerOperator.of(httpExchangeConfiguration.getCircuitBreaker())
                )
                .onErrorResume(throwable -> logAndHandleError(throwable, timerSample, path))
                .flatMap(response -> processResponse(
                        response,
                        timerSample,
                        responseClass,
                        bodyToLoggableString,
                        okResponseValidator,
                        returnIfResponseIsEmpty,
                        errorResponseMapper,
                        path,
                        method
                ))
                .contextWrite(context -> context.put(LOG_SUFFIX, atomicInteger.getAndIncrement() % 10000))
                ;
    }

    private WebClient.RequestHeadersSpec<? extends WebClient.RequestHeadersSpec<?>> getRequestSpec(URI uri,
                                                                                                   HttpMethod method,
                                                                                                   Object requestBody,
                                                                                                   Map<String, String> additionalHeaders) {

        Map<String, String> headers = mergeOverwriteHeaders(httpExchangeConfiguration.getStandardHeaders(), additionalHeaders);

        switch (method) {
            case GET: {
                if (Objects.nonNull(requestBody)) {
                    return webClient.method(HttpMethod.GET)
                            .uri(uri)
                            .headers(httpHeaders -> httpHeaders.setAll(headers))
                            .bodyValue(requestBody);
                }
                return webClient.method(HttpMethod.GET) //без дублирования не получается, так как тип RequestHeadersSpec не доступен вне своего пакета
                        .uri(uri)
                        .headers(httpHeaders -> httpHeaders.setAll(headers));
            }
            case POST:
                return webClient.post()
                        .uri(uri)
                        .headers(httpHeaders -> httpHeaders.setAll(headers))
                        .bodyValue(requestBody);
            case PUT:
                return webClient.put()
                        .uri(uri)
                        .headers(httpHeaders -> httpHeaders.setAll(headers))
                        .bodyValue(requestBody);
            case DELETE:
                if (Objects.nonNull(requestBody)) {
                    return webClient.method(HttpMethod.DELETE)
                            .uri(uri)
                            .headers(httpHeaders -> httpHeaders.setAll(headers))
                            .bodyValue(requestBody);
                }

                return webClient.method(HttpMethod.DELETE)
                        .uri(uri)
                        .headers(httpHeaders -> httpHeaders.setAll(headers));
            default:
                throw new RuntimeException("unsupported http method " + method);
        }
    }

    protected Map<String, String> mergeOverwriteHeaders(Map<String, String> baseHeaders, Map<String, String> additionalHeaders) {
        if (additionalHeaders == null || additionalHeaders.isEmpty()) {
            return baseHeaders;
        } else {
            if (baseHeaders.isEmpty()) {
                return additionalHeaders;
            }
            Map<String, String> map = new HashMap<>();
            map.putAll(additionalHeaders);
            map.putAll(baseHeaders);

            return map;
        }
    }

    private void logRequest(ContextView context, String path, HttpMethod method, String requestBody4log,
                            WebClient.RequestHeadersSpec<? extends WebClient.RequestHeadersSpec<?>> requestSpec) {
        final String logPrefix = getLogPrefixWithSuffix(context);

        String url = httpExchangeConfiguration.getBaseUrl() + path;

        final AtomicReference<Map<String, String>> headersToLog = new AtomicReference<>();
        requestSpec.headers(httpHeaders -> headersToLog.set(LoggingUtils.getSortedHeadersMap(httpHeaders)));

        LoggingUtils.logRequest(
                log,
                httpExchangeConfiguration.getLoggingName(),
                logPrefix,
                url,
                method,
                requestBody4log,
                headersToLog.get(),
                true
        );
    }

    private <T> Mono<T> processResponse(ClientResponse response,
                                        AtomicReference<Timer.Sample> timerSample,
                                        Class<T> responseClass,
                                        Function<T, String> bodyToLoggableString,
                                        Function2<ClientResponse, Optional<T>, Mono<Void>> okResponseValidator,
                                        T returnIfResponseIsEmpty,
                                        Function3<ClientResponse, Optional<T>, String, Mono<T>> errorResponseMapper,
                                        String path,
                                        HttpMethod httpMethod) {
        return response.bodyToMono(responseClass)
                .map(Optional::of)
                .switchIfEmpty(Mono.just(Optional.empty()))
                .flatMap(bodyOpt -> logResponse(response, bodyOpt, bodyToLoggableString, timerSample, path, httpMethod))
                .flatMap(bodyOpt -> processResponseStatusAndBody(response, path, bodyOpt, okResponseValidator, returnIfResponseIsEmpty, errorResponseMapper))
                ;
    }

    private <T> Mono<T> processResponseStatusAndBody(ClientResponse response, String path, Optional<T> bodyOpt,
                                                     Function2<ClientResponse, Optional<T>, Mono<Void>> okResponseValidator,
                                                     T returnIfResponseIsEmpty,
                                                     Function3<ClientResponse, Optional<T>, String, Mono<T>> errorResponseMapper) {
        if (response.statusCode().is2xxSuccessful()) {
            return okResponseValidator.map(response, bodyOpt)
                    .then(bodyOpt.map(Mono::just)
                            .orElse(Mono.just(returnIfResponseIsEmpty))
                    );
        } else {
            return errorResponseMapper.map(response, bodyOpt, path);
        }
    }

    private <T> Mono<Optional<T>> logResponse(ClientResponse response, Optional<T> body,
                                              Function<T, String> bodyToLoggableString,
                                              AtomicReference<Timer.Sample> timerSample,
                                              String path,
                                              HttpMethod httpMethod) {
        return Mono.deferContextual(Mono::just)
                .doOnSuccess(
                        context -> {
                            final long time = timerSample.get() == null
                                    ? -1
                                    : timerSample.get().stop(httpExchangeConfiguration.getTimer());
                            final HttpStatus statusCode = response.statusCode();
                            final String logPrefix = getLogPrefixWithSuffix(context);
                            final String responseBody = body.isPresent() ? bodyToLoggableString.apply(body.orElse(null)) : "";
                            // по аналогии чтобы как логгируются входящие запросы на сервис
                            // (тело отлавливается до логгирования самого статуса и тайминга ответа)

                            LoggingUtils.logResponseFromClient(
                                    log,
                                    httpExchangeConfiguration.getLoggingName(),
                                    logPrefix,
                                    time,
                                    statusCode,
                                    responseBody,
                                    path
                            );
                        })
                .then(Mono.just(body));
    }

    private Mono<ClientResponse> logAndHandleError(Throwable throwable, AtomicReference<Timer.Sample> timerSample, String path) {
        return Mono.deferContextual(Mono::just)
                .doOnSuccess(context -> {
                    final String logPrefix = getLogPrefixWithSuffix(context);
                    final long time = timerSample.get() == null ? -1 : timerSample.get().stop(httpExchangeConfiguration.getTimer());
                    LoggingUtils.logError(
                            log,
                            httpExchangeConfiguration.getLoggingName(),
                            logPrefix,
                            time,
                            "",
                            throwable,
                            path
                    );
                })
                .then(handleError(throwable, path));

    }

    private Mono<ClientResponse> handleError(Throwable throwable, String path) {
        return Mono.error(httpExchangeConfiguration.getHttpErrorHandler().handleError(path, throwable));
    }

    private String getLogPrefixWithSuffix(ContextView context) {
        return context.<String>getOrEmpty(LoggingUtils.LOG_PREFIX)
                .map(prefix -> context
                        .getOrEmpty(LOG_SUFFIX)
                        .map(suffix -> prefix + " key=[" + suffix + ']')
                        .orElse(prefix))
                .orElse("unknown");
    }
}
