package ru.nik.commons.http.errors.exceptions.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.nik.commons.http.errors.ErrorInfo;

public class NotFoundExceptionMapper implements ExceptionMapper {
    @Override
    public boolean canMap(Throwable t) {
        return t != null && t.getClass() == ResponseStatusException.class && ((ResponseStatusException) t).getStatus() == HttpStatus.NOT_FOUND;
    }

    @Override
    public ErrorInfo map(Throwable t) {
        return new ErrorInfo()
                .setTimestamp(System.currentTimeMillis())
                .setErrorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .setHttpStatus(HttpStatus.NOT_FOUND)
                .setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
    }
}
