package ru.nik.commons.http.errors.exceptions.mapper;

import org.springframework.http.HttpStatus;
import ru.nik.commons.http.errors.ErrorInfo;
import ru.nik.commons.http.errors.exceptions.model.ServiceException;

public class ServiceExceptionMapper implements ExceptionMapper {

    @Override
    public boolean canMap(Throwable t) {
        return t instanceof ServiceException;
    }

    @Override
    public ErrorInfo map(Throwable t) {
        return new ErrorInfo()
                .setTimestamp(System.currentTimeMillis())
                .setHttpStatus(HttpStatus.BAD_REQUEST)
                .setMessage(((ServiceException) t).getMessage())
                .setErrorCode(((ServiceException) t).getErrorCode());
    }

}
