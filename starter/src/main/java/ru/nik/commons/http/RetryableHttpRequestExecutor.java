package ru.nik.commons.http;

import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.nik.commons.http.config.HttpExchangeConfiguration;
import ru.nik.commons.http.errors.Function2;
import ru.nik.commons.http.errors.Function3;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class RetryableHttpRequestExecutor extends HttpRequestExecutorImpl {

    private final Retry retry;

    public RetryableHttpRequestExecutor(WebClient webClient,
                                        Logger log,
                                        HttpExchangeConfiguration config,
                                        Retry retry) {
        super(webClient, log, config);
        this.retry = retry;
    }

    @Override
    protected <T> Mono<T> doRequestWithLogConfigurable(
            URI uri,
            String path,
            Object request,
            String request4log,
            HttpMethod method,
            Map<String, String> additionalHeaders,
            Class<T> responseClass,
            Function<T, String> bodyToLoggableString,
            Function2<ClientResponse, Optional<T>, Mono<Void>> okResponseValidator,
            T returnIfResponseIsEmpty,
            Function3<ClientResponse, Optional<T>, String, Mono<T>> errorResponseMapper
    ) {
        return super.doRequestWithLogConfigurable(
                uri,
                path,
                request,
                request4log,
                method,
                additionalHeaders,
                responseClass,
                bodyToLoggableString,
                okResponseValidator,
                returnIfResponseIsEmpty,
                errorResponseMapper)
                .retryWhen(retry);
    }
}
