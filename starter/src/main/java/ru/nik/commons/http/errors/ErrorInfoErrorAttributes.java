package ru.nik.commons.http.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import ru.nik.commons.http.errors.exceptions.mapper.DefaultExceptionMapper;
import ru.nik.commons.http.errors.exceptions.mapper.ExceptionMapper;

import java.util.*;

/**
 * Предоставляет атрибуты ошибки в форме объекта ErrorInfo.
 */
public class ErrorInfoErrorAttributes extends DefaultErrorAttributes {
    public static final String ERROR_INFO_ATTRIBUTE = "_errorInfo";

    private static final String ERROR_ATTRIBUTE = ErrorInfoErrorAttributes.class.getName() + ".ERROR";

    private final List<ExceptionMapper> mappers;

    public ErrorInfoErrorAttributes(@Autowired List<ExceptionMapper> mappers) {
        this.mappers = new ArrayList<>();
        this.mappers.addAll(Optional.of(mappers).orElse(Collections.emptyList()));
        this.mappers.add(new DefaultExceptionMapper());
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        return Collections.singletonMap(ERROR_INFO_ATTRIBUTE, mapError(getError(request)));
    }


    @Override
    public Throwable getError(ServerRequest request) {
        return (Throwable) request.attribute(ERROR_ATTRIBUTE)
                .orElseThrow(() -> new IllegalStateException("Missing exception attribute in ServerWebExchange"));
    }

    @Override
    public void storeErrorInformation(Throwable error, ServerWebExchange exchange) {
        exchange.getAttributes().putIfAbsent(ERROR_ATTRIBUTE, error);
    }

    private ErrorInfo mapError(Throwable error) {
        return mappers.stream()
                .filter(mapper -> mapper.canMap(error))
                .map(mapper -> mapper.map(error))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no mappers found", error));
    }
}
