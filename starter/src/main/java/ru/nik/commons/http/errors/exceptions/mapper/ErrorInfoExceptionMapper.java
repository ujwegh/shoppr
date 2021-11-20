package ru.nik.commons.http.errors.exceptions.mapper;

import ru.nik.commons.http.errors.ErrorInfo;
import ru.nik.commons.http.errors.exceptions.model.ErrorInfoException;

public class ErrorInfoExceptionMapper implements ExceptionMapper {

    @Override
    public boolean canMap(Throwable t) {
        return t != null && t.getClass() == ErrorInfoException.class;
    }

    @Override
    public ErrorInfo map(Throwable t) {
        return ((ErrorInfoException) t).getErrorInfo();
    }

}
