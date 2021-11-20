package ru.nik.commons.http.internal;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nik.commons.http.config.HttpExchangeConfiguration;
import ru.nik.commons.http.config.ParametersHttpExchangeConfiguration;
import ru.nik.commons.retry.RetryProperties;
import ru.nik.commons.webflux.HttpErrorHandler;
import ru.nik.commons.webflux.handler.NoOpHttpErrorHandler;
import java.util.Map;
import java.util.Objects;

public class InternalRequestExecutors implements EnvironmentAware {

    private final WebClient webClient;
    private final RetryProperties retryProperties;
    private final InternalRequestExecutorErrorResponseMapper internalRequestExecutorErrorResponseMapper;

    private Environment environment;
    private final Cache<String, InternalRequestExecutor> executorsCache;
    private final HttpErrorHandler httpErrorHandler;

    public InternalRequestExecutors(WebClient webClient,
                                    RetryProperties retryProperties,
                                    InternalRequestExecutorErrorResponseMapper internalRequestExecutorErrorResponseMapper) {
        this(webClient, retryProperties, internalRequestExecutorErrorResponseMapper, new NoOpHttpErrorHandler());
    }

    public InternalRequestExecutors(WebClient webClient,
                                    RetryProperties retryProperties,
                                    InternalRequestExecutorErrorResponseMapper internalRequestExecutorErrorResponseMapper,
                                    HttpErrorHandler httpErrorHandler) {
        this.webClient = webClient;
        this.retryProperties = retryProperties;
        this.internalRequestExecutorErrorResponseMapper = internalRequestExecutorErrorResponseMapper;

        this.httpErrorHandler = httpErrorHandler;
        this.executorsCache = Caffeine.newBuilder().build();
    }

    public InternalRequestExecutor get(String serviceName,
                                       String urlEnvPropertyName) {
        return executorsCache.get(serviceName, _serviceName -> createExecutor(serviceName, urlEnvPropertyName, this.webClient, httpErrorHandler));
    }

    public InternalRequestExecutor get(String serviceName,
                                       String urlEnvPropertyName,
                                       WebClient webClient) {
        return executorsCache.get(serviceName, _serviceName -> createExecutor(serviceName, urlEnvPropertyName, webClient, httpErrorHandler));
    }

    public InternalRequestExecutor createExecutor(String serviceName,
                                                  String urlEnvPropertyName,
                                                  WebClient webClient,
                                                  HttpErrorHandler httpErrorHandler) {

        Objects.requireNonNull(serviceName, "serviceName is null");
        Objects.requireNonNull(urlEnvPropertyName, "urlEnvPropertyName is null");

        Map<String, String> standardHeaders = Map.of(
                HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE,
                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
        );

        HttpExchangeConfiguration configuration = ParametersHttpExchangeConfiguration.builder()
                .errorResponseMapper(internalRequestExecutorErrorResponseMapper::errorResponseMapper)
                .url(environment.getProperty(urlEnvPropertyName))
                .loggingName(serviceName + " CLIENT")
                .standardHeaders(standardHeaders)
                .httpErrorHandler(httpErrorHandler)
                .build();

        return new InternalRequestExecutor(webClient, retryProperties, configuration);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
