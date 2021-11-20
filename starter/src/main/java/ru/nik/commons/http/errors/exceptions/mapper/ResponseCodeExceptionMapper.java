package ru.nik.commons.http.errors.exceptions.mapper;


import ru.nik.commons.http.errors.ErrorInfo;
import ru.nik.commons.http.errors.ResponseCodeException;

public class ResponseCodeExceptionMapper implements ExceptionMapper {

    @Override
    public boolean canMap(Throwable t) {
        return t != null && t.getClass() == ResponseCodeException.class;
    }

    @Override
    public ErrorInfo map(Throwable t) {
        return ((ResponseCodeException) t).getErrorInfo();
    }

}
