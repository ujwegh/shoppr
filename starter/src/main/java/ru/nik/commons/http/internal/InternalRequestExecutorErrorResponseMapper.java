package ru.nik.commons.http.internal;

import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.config.ParametersHttpExchangeConfiguration;
import ru.nik.commons.http.errors.ErrorInfo;
import ru.nik.commons.http.errors.ResponseCode;
import ru.nik.commons.http.errors.ResponseCodeException;
import ru.nik.commons.http.mapper.ObjectJsonMapper;

import java.util.Optional;
import java.util.function.Function;

public class InternalRequestExecutorErrorResponseMapper {

    private final ObjectJsonMapper objectJsonMapper;

    public InternalRequestExecutorErrorResponseMapper(ObjectJsonMapper objectJsonMapper) {
        this.objectJsonMapper = objectJsonMapper;
    }

    public Mono<String> errorResponseMapper(ClientResponse clientResponse, Optional<String> bodyOptional, String path) {
        return bodyOptional
                .flatMap(body -> toErrorInfo(clientResponse, body))
                .flatMap(this::buildResponseCodeException)
                .map((Function<ResponseCodeException, Mono<String>>) Mono::error)
                .orElse(ParametersHttpExchangeConfiguration.defaultErrorResponseMapper(clientResponse, bodyOptional, path));
    }

    private Optional<ResponseCodeException> buildResponseCodeException(ErrorInfo errorInfo) {
        return ResponseCode
                .fromCode(errorInfo.getErrorCode())
                .map(responseCode -> new ResponseCodeException(responseCode, errorInfo));
    }

    private Optional<ErrorInfo> toErrorInfo(ClientResponse clientResponse, String objAsString) {
        try {
            ErrorInfo errorInfo = objectJsonMapper
                    .toObject(objAsString, ErrorInfo.class)
                    .setHttpStatus(clientResponse.statusCode());

            return Optional.of(errorInfo);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
