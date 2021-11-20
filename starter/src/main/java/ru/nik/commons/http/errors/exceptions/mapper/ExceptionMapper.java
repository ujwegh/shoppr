package ru.nik.commons.http.errors.exceptions.mapper;


import ru.nik.commons.http.errors.ErrorInfo;

public interface ExceptionMapper {
    String DEFAULT_ERROR_MESSAGE = "OBA_defaultError_message";

    /**
     * Returns <code>true</code> if current mapper can map throwable.
     * @param t throwable.
     * @return <code>true</code> if current mapper can map throwable.
     */
    boolean canMap(Throwable t);

    /**
     * Returns error info.
     * @param t throwable.
     * @return mapped error info.
     */
    ErrorInfo map(Throwable t);
}
