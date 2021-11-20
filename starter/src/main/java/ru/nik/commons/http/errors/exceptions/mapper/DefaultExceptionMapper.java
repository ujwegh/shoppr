package ru.nik.commons.http.errors.exceptions.mapper;

import ru.nik.commons.http.errors.ErrorInfo;
import ru.nik.commons.http.errors.ResponseCode;

public class DefaultExceptionMapper implements ExceptionMapper {
    @Override
    public boolean canMap(Throwable t) {
        return t != null;
    }

    @Override
    public ErrorInfo map(Throwable t) {
        return new ErrorInfo()
                .setTimestamp(System.currentTimeMillis())
                .setHttpStatus(ResponseCode.SYSTEM_ERROR.getHttpStatus())
                .setErrorCode(ResponseCode.SYSTEM_ERROR.getCode())
                .setMessage(DEFAULT_ERROR_MESSAGE);
    }
}
