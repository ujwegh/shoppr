package ru.nik.commons.http.errors;

import org.springframework.http.HttpStatus;

public interface ResponseCodeError {

    String getCode();
    String getMessage();
    HttpStatus getHttpStatus();
    boolean isLocale();
}
