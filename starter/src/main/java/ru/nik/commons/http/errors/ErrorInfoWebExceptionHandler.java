package ru.nik.commons.http.errors;

import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.logging.LoggingUtils;
import ru.nik.commons.http.logging.LoggingWebFilter;

import java.util.*;

@Slf4j
public class ErrorInfoWebExceptionHandler implements ErrorWebExceptionHandler {
    /**
     * Currently duplicated from Spring WebFlux HttpWebHandlerAdapter.
     */
    private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS = new HashSet<>(
            Arrays.asList("ClientAbortException", "EOFException", "EofException")
    );

    public static final Set<String> RESPONSE_CODE_WARN_LOG_LEVEL = Set.of("SESSION_INVALIDATED");

    protected static final String LAST_RESORT_ERROR_MESSAGE = "Произошла ошибка, попробуйте позже или обратитесь в службу поддержки";

    private final ErrorAttributes errorAttributes;
    private final List<HttpMessageReader<?>> messageReaders;
    private final List<HttpMessageWriter<?>> messageWriters;
    private final List<ViewResolver> viewResolvers = Collections.emptyList();

    protected final ErrorInfoResolver errorInfoResolver;

    public ErrorInfoWebExceptionHandler(ErrorAttributes errorAttributes,
                                        List<HttpMessageReader<?>> messageReaders,
                                        List<HttpMessageWriter<?>> messageWriters,
                                        ErrorInfoResolver errorInfoResolver) {
        this.errorAttributes = errorAttributes;
        this.messageReaders = messageReaders;
        this.messageWriters = messageWriters;
        this.errorInfoResolver = errorInfoResolver;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        if (exchange.getResponse().isCommitted() || isDisconnectedClientError(throwable)) {
            return Mono.error(throwable);
        }
        errorAttributes.storeErrorInformation(throwable, exchange);
        String logPrefix = exchange.getAttribute(LoggingUtils.LOG_PREFIX);
        if (logPrefix == null) {
            // что-то прошло мимо LoggingWebFilter (врядли), либо ошибка в самом LoggingWebFilter
            log.error("unknown request with error", throwable);
        }
        final ServerWebExchange mutatedExchange = LoggingWebFilter.mutateToLogBodies(exchange, logPrefix, true, log, false);
        ServerRequest request = ServerRequest.create(mutatedExchange, messageReaders);
        return getRoutingFunction(mutatedExchange)
                .route(request)
                .switchIfEmpty(Mono.error(throwable))
                .flatMap((handler) -> handler.handle(request))
                .flatMap((response) -> write(mutatedExchange, response));
    }

    private boolean isDisconnectedClientError(Throwable ex) {
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
        message = (message != null) ? message.toLowerCase() : "";
        String className = ex.getClass().getSimpleName();
        return (message.contains("broken pipe") || DISCONNECTED_CLIENT_EXCEPTIONS.contains(className));
    }

    private Mono<? extends Void> write(ServerWebExchange exchange, ServerResponse response) {
        // force content-type since writeTo won't overwrite response header values
        exchange.getResponse().getHeaders().setContentType(response.headers().getContentType());
        return response.writeTo(exchange, new ResponseContext());
    }

    private RouterFunction<ServerResponse> getRoutingFunction(ServerWebExchange exchange) {
        return RouterFunctions.route(RequestPredicates.all(), request -> renderErrorResponse(exchange, request));
    }

    private Mono<ServerResponse> renderErrorResponse(ServerWebExchange exchange, ServerRequest request) {

        final Throwable throwable = this.errorAttributes.getError(request);
        final String logPrefix = (String) exchange.getAttributes().get(LoggingUtils.LOG_PREFIX);
        final boolean wasExcluded = LoggingWebFilter.wasExcluded(exchange);
        final long time = exchange.<Timer.Sample>getAttribute(LoggingWebFilter.TIMER_SAMPLE)
                .stop(exchange.getAttribute(LoggingWebFilter.TIMER));
        final Map<String, Object> errorPropertiesMap = this.errorAttributes.getErrorAttributes(request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.values()));

        return mapErrorInfo(getErrorInfo(throwable, errorPropertiesMap))
                .flatMap(mappedErrorInfo ->
                        buildErrorInfoServerResponse(request, throwable, logPrefix, wasExcluded, time, mappedErrorInfo));
    }

    private ErrorInfo getErrorInfo(Throwable throwable, Map<String, Object> errorPropertiesMap) {

        Object errorInfo = errorPropertiesMap.get(ErrorInfoErrorAttributes.ERROR_INFO_ATTRIBUTE);

        if (errorInfo == null) {
            return new ErrorInfo()
                    .setMessage("DEFAULT_ERROR_MESSAGE")
                    .setReason(throwable.getMessage())
                    .setResolveLocale(true);
        }

        return (ErrorInfo) errorInfo;
    }

    protected Mono<ErrorInfo> mapErrorInfo(ErrorInfo errorInfo) {
        if (errorInfo.isResolveLocale()) {
            return resolveKeyByMessage(errorInfo);
        } else {
            return Mono.just(errorInfo);
        }
    }

    private Mono<ServerResponse> buildErrorInfoServerResponse(ServerRequest request,
                                                              Throwable throwable,
                                                              String logPrefix,
                                                              boolean wasExcluded,
                                                              long time,
                                                              ErrorInfo mappedErrorInfo) {
        return ServerResponse
                .status(mappedErrorInfo.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mappedErrorInfo)
                .doOnSuccess(serverResponse -> {
                    if (wasExcluded) {
                        LoggingUtils.logIgnoredError(log, request.uri().toString(), throwable);
                    }
                    if (needLogAsWarn(mappedErrorInfo)) {
                        LoggingUtils.logWarn(log, "", logPrefix, time, String.valueOf(serverResponse.statusCode()), throwable, request.path());
                    } else {
                        if (HttpStatus.NOT_FOUND.equals(mappedErrorInfo.getHttpStatus())) {
                            LoggingUtils.logRequest(log, "", logPrefix, request.path(), HttpMethod.valueOf(request.methodName()), request.headers().asHttpHeaders().toSingleValueMap());
                        }
                        LoggingUtils.logError(log, "", logPrefix, time, String.valueOf(serverResponse.statusCode()), throwable, request.path());
                    }
                });
    }

    private boolean needLogAsWarn(ErrorInfo errorInfo) {
        String errorCode = errorInfo.getErrorCode();
        HttpStatus httpStatus = errorInfo.getHttpStatus();
        Boolean isCodeWarnContains = Optional
                .ofNullable(errorCode)
                .map(RESPONSE_CODE_WARN_LOG_LEVEL::contains)
                .orElse(false);
        boolean isUnauthorized = HttpStatus.UNAUTHORIZED.equals(httpStatus);

        return isCodeWarnContains || isUnauthorized;
    }

    private Mono<ErrorInfo> resolveKeyByMessage(ErrorInfo errorInfo) {
        return errorInfoResolver
                .resolveError(errorInfo.getMessage())
                .map(localeValue -> enrichErrorInfoCustomMessage(errorInfo, localeValue))
                .switchIfEmpty(Mono.just(enrichErrorInfoDefaultMessage(errorInfo)))
                .onErrorResume(th -> LoggingUtils
                        .logErrorCustomMono(log, "", "failed to get locale value", th)
                        .then(Mono.just(enrichErrorInfoDefaultMessage(errorInfo))));

    }

    private ErrorInfo enrichErrorInfoCustomMessage(ErrorInfo errorInfo, String localeValue) {
        return errorInfo.setMessage(localeValue);
    }

    private ErrorInfo enrichErrorInfoDefaultMessage(ErrorInfo errorInfo) {
        return errorInfo.setMessage(LAST_RESORT_ERROR_MESSAGE);
    }

    private class ResponseContext implements ServerResponse.Context {
        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return ErrorInfoWebExceptionHandler.this.messageWriters;
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return ErrorInfoWebExceptionHandler.this.viewResolvers;
        }
    }
}
