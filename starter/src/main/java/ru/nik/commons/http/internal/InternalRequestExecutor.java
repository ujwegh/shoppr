package ru.nik.commons.http.internal;

import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;
import reactor.util.retry.Retry;
import ru.nik.commons.http.LoggingUtils;
import ru.nik.commons.http.RetryableHttpRequestExecutor;
import ru.nik.commons.http.config.HttpExchangeConfiguration;
import ru.nik.commons.retry.RetryProperties;

import java.net.ConnectException;
import java.time.Duration;
import java.util.Map;

import static ru.nik.commons.http.LoggingUtils.addUniqueRequestKey;

@Slf4j
public class InternalRequestExecutor extends RetryableHttpRequestExecutor {

    public InternalRequestExecutor(WebClient webClient,
                                   RetryProperties retryProperties,
                                   HttpExchangeConfiguration configuration) {
        super(webClient,
                log,
                configuration,
                retryIfServiceUnavailable(retryProperties));
    }

    private static Retry retryIfServiceUnavailable(RetryProperties retryProperties) {
        return Retry
                .fixedDelay(retryProperties.getMaxAttempts(), Duration.ofMillis(retryProperties.getBackoff()))
                .filter(InternalRequestExecutor::shouldRetry)
                .doBeforeRetry(retrySignal -> log.info("Retry: {}", retrySignal.failure().getMessage()));
    }

    private static boolean shouldRetry(Throwable exception) {
        if (exception instanceof HttpClientErrorException
                && ((HttpClientErrorException) exception).getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
            return true;
        }
        if (exception instanceof ConnectTimeoutException) {
            return true;
        }
        return exception instanceof ConnectException;
    }

    @Override
    public Mono<String> doGETRequest(String path, Map<String, String> additionalHeaders) {
        return Mono.deferContextual(contextView -> super.doGETRequest(
                path,
                mergeOverwriteHeaders(additionalHeaders, clientAdditionalHeaders(contextView))
        ));
    }

    @Override
    public Mono<String> doPOSTRequest(String path, String request, Map<String, String> additionalHeaders) {
        return Mono.deferContextual(contextView ->
                super.doPOSTRequest(
                        path, request,
                        mergeOverwriteHeaders(additionalHeaders, clientAdditionalHeaders(contextView))
                ));
    }

    @Override
    public Mono<String> doPOSTRequest(String path, byte[] request, Map<String, String> additionalHeaders) {
        return Mono.deferContextual(contextView ->
                super.doPOSTRequest(
                        path, request,
                        mergeOverwriteHeaders(additionalHeaders, clientAdditionalHeaders(contextView))
                ));
    }

    @Override
    public Mono<String> doPOSTMultiPartRequest(String path, MultiValueMap<String, HttpEntity<?>> request, Map<String, String> additionalHeaders) {
        return Mono.deferContextual(contextView ->
                super.doPOSTMultiPartRequest(
                        path, request,
                        mergeOverwriteHeaders(additionalHeaders, clientAdditionalHeaders(contextView))
                ));
    }

    @Override
    public Mono<String> doPUTRequest(String path, String request, String request4log, Map<String, String> additionalHeaders) {
        return Mono.deferContextual(contextView ->
                super.doPUTRequest(
                        path, request, request4log,
                        mergeOverwriteHeaders(additionalHeaders, clientAdditionalHeaders(contextView))
                ));
    }

    @Override
    public Mono<String> doDELETERequest(String path, Map<String, String> additionalHeaders) {
        return Mono.deferContextual(contextView ->
                super.doDELETERequest(
                        path,
                        mergeOverwriteHeaders(additionalHeaders, clientAdditionalHeaders(contextView))
                ));
    }

    @Override
    public Mono<String> doDELETERequest(String path,
                                        String request,
                                        String request4log,
                                        Map<String, String> additionalHeaders) {
        return Mono.deferContextual(contextView ->
                super.doDELETERequest(
                        path,
                        request,
                        request4log,
                        mergeOverwriteHeaders(additionalHeaders, clientAdditionalHeaders(contextView))
                ));
    }

    private Map<String, String> clientAdditionalHeaders(ContextView context) {
        return Map.of(LoggingUtils.LOG_PREFIX, addUniqueRequestKey(context.<String>getOrEmpty(LoggingUtils.LOG_PREFIX).orElse("")));
    }
}
