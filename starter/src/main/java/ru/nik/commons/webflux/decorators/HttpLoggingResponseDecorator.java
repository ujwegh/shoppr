package ru.nik.commons.webflux.decorators;

import io.micrometer.core.instrument.Timer;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.logging.LoggingUtils;

import java.util.List;
import java.util.function.Function;

import static ru.nik.commons.http.logging.LoggingUtils.UNKNOWN;
import static ru.nik.commons.http.logging.LoggingWebFilter.*;


public class HttpLoggingResponseDecorator extends ServerHttpResponseDecorator {

    public static final String MDC_KEY_URI = "uri";
    public static final String MDC_KEY_DURATION = "duration";
    public static final String MDC_KEY_HTTP_STATUS_CODE = "httpStatusCode";

    private final ServerWebExchange exchange;
    private final Logger log;

    private final boolean responseForErrorHandler;
    private final String uriPath;
    private final String logPrefix;
    private final boolean isGateway;

    public HttpLoggingResponseDecorator(ServerWebExchange exchange,
                                        Logger log,
                                        boolean responseForErrorHandler,
                                        String uriPath,
                                        String logPrefix,
                                        boolean isGateway) {
        super(exchange.getResponse());
        this.exchange = exchange;
        this.log = log;
        this.responseForErrorHandler = responseForErrorHandler;
        this.uriPath = uriPath;
        this.logPrefix = logPrefix;
        this.isGateway = isGateway;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (wasExcluded(exchange)) {
            return super.writeWith(body);
        }

        return Flux.from(body)
                .collectList()
                .doOnNext(dataBuffers -> {
                            HttpStatus httpStatusCode = exchange.getResponse().getStatusCode();
                            String prefix = getPrefix(httpStatusCode);

                            Timer timer = exchange.getAttribute(TIMER);
                            long time = exchange.<Timer.Sample>getAttribute(TIMER_SAMPLE).stop(timer);
                            String responseAsString = LoggingUtils.getResponseAsString((List<DataBuffer>) dataBuffers, "REQUEST ", logPrefix, log);

                            /*
                             Добавление в MDC контекст полей для отправки в грейлог отдельных полей,
                             по которым строятся графики
                             */
                            addToMdcCommonFields(httpStatusCode, time);

                            LoggingUtils.logResponse(log, prefix, logPrefix, time, httpStatusCode, responseAsString, "", !isGateway);

                            /*
                            Очистка MDC контекста от полей для постройки графика после логирования,
                            чтобы при логировании других логов - в грейлог не отсылались данные поля
                             */
                            clearMdcCommonFields();
                        }
                )
                .flatMap(dataBuffers -> super.writeWith(Flux.fromIterable(dataBuffers)));
    }

    private String getPrefix(HttpStatus httpStatusCode) {
        String prefix = responseForErrorHandler ? "ERROR " : "";
        if (HttpStatus.UNAUTHORIZED.equals(httpStatusCode)) {
            prefix = "WARN ";
        }
        return prefix;
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        if (wasExcluded(exchange)) {
            return super.writeAndFlushWith(body);
        }

        return Flux.from(body)
                .flatMap(Function.identity())
                .collectList()
                .doOnNext(dataBuffers -> logInterceptedBody((List<DataBuffer>) dataBuffers,
                        responseForErrorHandler ? "ERROR " : "RESPONSE ", logPrefix, log))
                .flatMap(dataBuffers -> super.writeAndFlushWith(Mono.just(Flux.fromIterable(dataBuffers))))
                .then();
    }

    private void addToMdcCommonFields(HttpStatus httpStatusCode, long duration) {
        if (StringUtils.isNotBlank(uriPath)) {
            MDC.put(MDC_KEY_URI, uriPath);
        }

        String parseDuration = parseDuration(duration);
        if (!UNKNOWN.equals(parseDuration)) {
            MDC.put(MDC_KEY_DURATION, parseDuration);
        }

        if (httpStatusCode != null) {
            MDC.put(MDC_KEY_HTTP_STATUS_CODE, String.valueOf(httpStatusCode.value()));
        }
    }

    private String parseDuration(long time) {
        return time > 0 ? String.valueOf(time / 1000000000D) : UNKNOWN;
    }

    private void clearMdcCommonFields() {
        MDC.remove(MDC_KEY_URI);
        MDC.remove(MDC_KEY_DURATION);
        MDC.remove(MDC_KEY_HTTP_STATUS_CODE);
    }

    private static void logInterceptedBody(List<DataBuffer> dataBuffers, String bodyType,
                                           String logPrefix, Logger logger) {
        String responseAsString = LoggingUtils.getResponseAsString(dataBuffers, bodyType, logPrefix, logger);
        LoggingUtils.logBody(logger, bodyType, logPrefix, responseAsString);
    }
}
