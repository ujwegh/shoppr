package ru.nik.commons.webflux;

public interface HttpErrorHandler {

    Throwable handleError(String path, Throwable cause);

}
