package ru.nik.commons.webflux.decorators;

import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import ru.nik.commons.http.logging.LoggingUtils;

import java.util.Collections;
import java.util.Map;

import static ru.nik.commons.http.logging.LoggingWebFilter.LOG_HEADERS;
import static ru.nik.commons.http.logging.LoggingWebFilter.wasExcluded;

public class HttpLoggingRequestDecorator extends ServerHttpRequestDecorator {

    private final ServerWebExchange exchange;
    private final Logger log;
    private final String uriPath;
    private final String logPrefix;

    public HttpLoggingRequestDecorator(ServerWebExchange exchange,
                                       Logger log,
                                       String uriPath,
                                       String logPrefix) {
        super(exchange.getRequest());
        this.exchange = exchange;
        this.log = log;
        this.uriPath = uriPath;
        this.logPrefix = logPrefix;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        if (wasExcluded(exchange)) {
            return super.getBody();
        }

        return super.getBody()
                .collectList()
                .flatMapIterable(dataBuffers -> {
                    boolean needLogHeaders = exchange.getAttributeOrDefault(LOG_HEADERS, false);
                    String responseAsString = LoggingUtils.getResponseAsString(dataBuffers, "REQUEST ", logPrefix, log);
                    HttpMethod httpMethod = exchange.getRequest().getMethod();
                    Map<String, String> httpHeaders = needLogHeaders ?
                            LoggingUtils.getSortedHeadersMap(exchange.getRequest().getHeaders())
                            :
                            Collections.emptyMap();

                    LoggingUtils.logRequest(log, "", logPrefix, uriPath, httpMethod, responseAsString, httpHeaders);

                    return dataBuffers;
                });
    }


}
