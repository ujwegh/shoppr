package ru.nik.commons.http.logging;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@UtilityClass
public class LoggingUtils {
    public static final String UNKNOWN = "unknown";
    public static final String LOG_PREFIX = "LOG_PREFIX";
    private static final String LOG_FORMAT = "{} {}";

    private static final AtomicInteger requestCounter = new AtomicInteger(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));

    private static final int MAX_RESPONSE_BODY_LENGTH = 10000;

    public static String addUniqueRequestKey(String logPrefix) {
        return logPrefix + " (" + requestCounter.incrementAndGet() % 10000 + ")";
    }

    public static Map<String, String> getSortedHeadersMap(HttpHeaders httpHeaders) {
        if (httpHeaders == null || httpHeaders.isEmpty()) {
            return Collections.emptyMap();
        }
        return httpHeaders.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.join(",", e.getValue()), (u, v) -> u, LinkedHashMap::new));
    }

    public static void logRequest(Logger log,
                                  String requestTypePrefix,
                                  String logPrefix,
                                  String uri,
                                  HttpMethod method) {
        logRequest(log, requestTypePrefix, logPrefix, uri, method, "", Collections.emptyMap());
    }

    public static void logRequest(Logger log,
                                  String logPrefix,
                                  String uri,
                                  HttpMethod method,
                                  Map<String, String> headers) {
        logRequest(log, "", logPrefix, uri, method, "", headers);
    }

    public static void logRequest(Logger log,
                                  String requestTypePrefix,
                                  String logPrefix,
                                  String uri,
                                  HttpMethod method,
                                  String body) {
        logRequest(log, requestTypePrefix, logPrefix, uri, method, body, Collections.emptyMap());
    }

    public static void logRequest(Logger log,
                                  String requestTypePrefix,
                                  String logPrefix,
                                  String uri,
                                  HttpMethod method,
                                  Map<String, String> headers) {
        logRequest(log, requestTypePrefix, logPrefix, uri, method, "", headers);
    }

    public static void logRequest(Logger log,
                                  String requestTypePrefix,
                                  String logPrefix,
                                  String uri,
                                  HttpMethod method,
                                  String body,
                                  Map<String, String> headers) {
        logRequest(log, requestTypePrefix, logPrefix, uri, method, body, headers, false);
    }


    public static void logRequest(Logger log,
                                  String requestTypePrefix,
                                  String logPrefix,
                                  String uri,
                                  HttpMethod method,
                                  String body,
                                  Map<String, String> headers,
                                  boolean isLogBody) {
        StringBuilder message = new StringBuilder(String.format("%s REQUEST [%s]: [%s] %s ", requestTypePrefix, logPrefix, method, uri));
        enrichHeaders(headers, message);

        if (log.isDebugEnabled() || isLogBody) {
            if (StringUtils.isNotBlank(body)) {
                message.append(String.format("BODY: %s", body));
            } else if (!HttpMethod.GET.equals(method)) {
                message.append("BODY IS EMPTY");
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(message.toString());
        } else {
            log.info(message.toString());
        }
    }

    public static void enrichHeaders(Map<String, String> headers, StringBuilder message) {
        if (message == null
                || headers == null
                || headers.isEmpty()) {
            return;
        }

        headers.forEach((header, value) -> message.append(" ")
                .append(header)
                .append(": ")
                .append(value)
        );

        message.append(" ");
    }

    public static void logRequestCustomForProxy(Logger log, String requestTypePrefix, String logPrefix, String customMessage) {
        log.info(String.format("%s REQUEST [%s]: %s", requestTypePrefix, logPrefix, customMessage));
    }

    public static Mono<Void> logErrorCustomMono(Logger log, String customPrefix, String customMessage, Throwable throwable) {
        return Mono.deferContextual(Mono::just)
                .doOnSuccess(context ->
                        logErrorCustom(log, customPrefix, context, customMessage, throwable)
                ).then();
    }

    public static void logErrorCustom(Logger log, String customPrefix, ContextView context, String customMessage, Throwable throwable) {
        String format = String.format("%s [%s]: %s", customPrefix, context.getOrDefault(LOG_PREFIX, ""), customMessage);
        log.error(format, throwable);
    }

    public static void logError(Logger log,
                                String errorTypePrefix,
                                String logPrefix,
                                long timeNano,
                                String responseHttpStatus,
                                Throwable throwable) {
        log.error(String.format("%s ERROR [%s]: [%s] [%s] %s",
                errorTypePrefix,
                logPrefix,
                timeNano > 0 ? String.valueOf(timeNano / 1000000000D) : UNKNOWN,
                responseHttpStatus,
                getThrowableInfo(throwable)
        ), throwable);
    }

    public static void logError(Logger log,
                                String errorTypePrefix,
                                String logPrefix,
                                long timeNano,
                                String responseHttpStatus,
                                Throwable throwable,
                                String url) {
        log.error(String.format("%s ERROR [%s]: [%s] [%s] [%s] %s",
                errorTypePrefix,
                logPrefix,
                timeNano > 0 ? String.valueOf(timeNano / 1000000000D) : UNKNOWN,
                responseHttpStatus,
                url,
                getThrowableInfo(throwable)
        ), throwable);
    }

    public static void logError(Logger log,
                                String errorTypePrefix,
                                long timeNano,
                                String responseHttpStatus,
                                Throwable throwable) {
        log.error(String.format("%s ERROR: [%s] [%s] %s",
                errorTypePrefix,
                timeNano > 0 ? String.valueOf(timeNano / 1000000000D) : UNKNOWN,
                responseHttpStatus,
                getThrowableInfo(throwable)
        ), throwable);
    }

    public static void logWarn(Logger log,
                               String errorTypePrefix,
                               String logPrefix,
                               long timeNano,
                               String responseHttpStatus,
                               Throwable throwable,
                               String url) {
        log.warn(String.format("%s WARN [%s]: [%s] [%s] [%s] %s",
                errorTypePrefix,
                logPrefix,
                timeNano > 0 ? String.valueOf(timeNano / 1000000000D) : UNKNOWN,
                responseHttpStatus,
                url,
                getThrowableInfo(throwable)
        ), throwable);
    }

    public static String getThrowableInfo(Throwable throwable) {
        return throwable == null ? "" : throwable.getClass().getName() + ": " + throwable.getMessage();
    }

    public static void logCancel(Logger log, String cancelTypePrefix,
                                 String logPrefix, long timeNano) {
        log.info(String.format("%s CANCELLED [%s]: [%s]",
                cancelTypePrefix,
                logPrefix,
                timeNano > 0 ? String.valueOf(timeNano / 1000000000D) : UNKNOWN
        ));
    }

    public static void logCancel(Logger log, HttpMethod httpMethod,
                                 String logPrefix, long timeNano,
                                 String url) {
        log.info(String.format("%s CANCELLED [%s]: [%s] [%s]",
                httpMethod,
                url,
                logPrefix,
                timeNano > 0 ? String.valueOf(timeNano / 1000000000D) : UNKNOWN
        ));
    }

    public static void logIgnoredCancel(Logger log, String uri) {
        log.info(String.format("IGNORED REQUEST CANCELLED: [%s]", uri));
    }

    public static void logIgnoredError(Logger log, String uri, Throwable t) {
        log.error(String.format("IGNORED REQUEST ERROR: [%s]", uri), t);
    }

    public static void logResponse(Logger log,
                                   String responseTypePrefix,
                                   long timeNano,
                                   HttpStatus responseHttpStatus) {
        logResponse(log, responseTypePrefix, "", timeNano, responseHttpStatus, "");
    }

    public static void logResponse(Logger log,
                                   String responseTypePrefix,
                                   String logPrefix,
                                   long timeNano,
                                   HttpStatus responseHttpStatus) {
        logResponse(log, responseTypePrefix, logPrefix, timeNano, responseHttpStatus, "");
    }

    public static void logResponse(Logger log,
                                   String responseTypePrefix,
                                   String logPrefix,
                                   long timeNano,
                                   HttpStatus responseHttpStatus,
                                   String responseBody) {
        logResponse(log, responseTypePrefix, logPrefix, timeNano, responseHttpStatus, responseBody, "", true);
    }

    public static void logResponseFromClient(Logger log,
                                             String responseTypePrefix,
                                             String logPrefix,
                                             long timeNano,
                                             HttpStatus responseHttpStatus,
                                             String responseBody,
                                             String path) {
        logResponse(log, responseTypePrefix, logPrefix, timeNano, responseHttpStatus, responseBody, path, true);
    }

    public static void logResponse(Logger log,
                                   String responseTypePrefix,
                                   String logPrefix,
                                   long timeNano,
                                   HttpStatus responseHttpStatus,
                                   String responseBody,
                                   String path,
                                   boolean isLogBody) {
        StringBuilder message = new StringBuilder(String.format("%s RESPONSE [%s]: [%s] [%s] [%s] ",
                responseTypePrefix,
                logPrefix,
                timeNano > 0 ? String.valueOf(timeNano / 1000000000D) : UNKNOWN,
                responseHttpStatus,
                path
        ));

        if (log.isDebugEnabled() || isLogBody) {
            if (StringUtils.isNotBlank(responseBody)) {
                if (responseBody.length() <= MAX_RESPONSE_BODY_LENGTH) {
                    message.append("RESPONSE BODY: ").append(responseBody);
                } else {
                    String croppedBody = responseBody.substring(0, MAX_RESPONSE_BODY_LENGTH);
                    message.append("RESPONSE BODY: ").append(croppedBody);
                }
            } else {
                message.append("RESPONSE BODY IS EMPTY");
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(message.toString());
        } else {
            log.info(message.toString());
        }
    }

    public static void logBody(Logger log, String bodyTypePrefix, String logPrefix, String body) {
        if (log.isDebugEnabled()) {
            if (body.length() <= MAX_RESPONSE_BODY_LENGTH) {
                log.info(String.format("%s BODY [%s]: [%s]", bodyTypePrefix, logPrefix, body));
            } else {
                String croppedBody = body.substring(0, MAX_RESPONSE_BODY_LENGTH);
                log.info(String.format("%s BODY [%s]: [%s]", bodyTypePrefix, logPrefix, croppedBody));

            }
        }
    }

    public static Mono<Void> logDebug(Logger log, Context context, String message) {
        return Mono.just(context.getOrEmpty(LOG_PREFIX).orElse(""))
                .doOnSuccess(prefix -> {
                    log.debug(LOG_FORMAT, prefix, message);
                })
                .then();
    }

    public static Mono<String> logExchange(Logger log, Context context, String message, String requestUri,
                                           HttpStatus httpStatus, Long duration, String requestBody,
                                           String responseBody) {
        return logExchange(log, context.<String>getOrEmpty(LOG_PREFIX).orElse(""),
                message, requestUri, httpStatus.value(), httpStatus.getReasonPhrase(),
                duration, requestBody, responseBody);
    }

    public static Mono<String> logExchange(Logger log, String logPrefix, String message, String requestUri,
                                           Integer statusCode, String statusMessage, Long duration, String requestBody,
                                           String responseBody) {
        return Mono.just(responseBody)
                .doOnSuccess(ignored -> {
                    log.info("{} {} uri=[{}]; status=[{}]; duration=[{}]; requestBody=[{}]; responseBody=[{}]",
                            logPrefix,
                            message,
                            requestUri,
                            Optional.of(statusCode).map(Object::toString).orElse(""),
                            duration / 1000000000D,
                            Optional.of(requestBody).orElse(""),
                            Optional.of(responseBody).orElse(""));
                });
    }

    public static Mono<Void> logErrorExchange(Logger log, Context context, String message, String requestUri,
                                              HttpStatus httpStatus, Long duration, String requestBody,
                                              String responseBody, Throwable throwable) {
        return Mono.just(requestUri)
                .doOnSuccess(ignored ->
                {
                    log.error("{} {} uri=[{}]; status=[{}]; duration=[{}]; requestBody=[{}]; responseBody=[{}]",
                            context.getOrEmpty(LOG_PREFIX).orElse(""),
                            message,
                            requestUri,
                            Optional.of(httpStatus).map(HttpStatus::value).map(Object::toString).orElse(""),
                            duration / 1000000000D,
                            Optional.of(requestBody).orElse(""),
                            Optional.of(responseBody).orElse(""),
                            throwable);
                })
                .then();
    }

    public static Mono<Void> logError(Logger log, String message, Throwable throwable) {
        return Mono.deferContextual(Mono::just)
                .flatMap(context -> logError(log, context, message, throwable));
    }

    public static Mono<Void> logError(Logger log, ContextView context, String message, Throwable throwable) {
        return Mono.just(context.getOrEmpty(LOG_PREFIX).orElse(""))
                .doOnSuccess(prefix -> {
                    String format = String.format("%s %s", prefix, message);
                    log.error(format, throwable);
                })
                .then();
    }

    public static String getResponseAsString(List<DataBuffer> dataBuffers,
                                             String bodyType,
                                             String logPrefix,
                                             Logger logger) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dataBuffers.forEach(dataBuffer -> {
            try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
            } catch (IOException e) {
                logger.error(String.format("%s BODY INTERCEPTION ERROR [%s]", bodyType, logPrefix), e);
            }
        });

        return baos.toString(StandardCharsets.UTF_8);
    }

    public static Context applyLoggingPrefix(ServerRequest request, Context context) {
        return context.put(LOG_PREFIX,
                Optional.of(request.headers().header(LOG_PREFIX))
                        .map(strings -> strings.stream().findFirst().orElse(""))
                        .orElse(""));
    }

}

