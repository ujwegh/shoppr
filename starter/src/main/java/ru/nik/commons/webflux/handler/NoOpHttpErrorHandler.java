package ru.nik.commons.webflux.handler;


import ru.nik.commons.webflux.HttpErrorHandler;

public class NoOpHttpErrorHandler implements HttpErrorHandler {

    @Override
    public Throwable handleError(String path, Throwable cause) {
        return cause;
    }

}
