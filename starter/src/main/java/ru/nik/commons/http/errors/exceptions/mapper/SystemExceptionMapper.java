package ru.nik.commons.http.errors.exceptions.mapper;

import ru.nik.commons.http.errors.ErrorInfo;
import ru.nik.commons.http.errors.exceptions.model.SystemException;

public class SystemExceptionMapper implements ExceptionMapper {

    @Override
    public boolean canMap(Throwable t) {
        return t != null && t.getClass() == SystemException.class;
    }

    @Override
    public ErrorInfo map(Throwable t) {
        return ((SystemException) t).getErrorInfo();
    }

}
