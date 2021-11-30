package ru.nik.products.exceptions;

import org.springframework.http.HttpStatus;
import ru.nik.commons.http.errors.ResponseCodeError;

public class ExampleErrorException implements ResponseCodeError {
    @Override
    public String getCode() {
        return "EXAMPLE_ERROR";
    }

    @Override
    public String getMessage() {
        return "Some-message";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public boolean isLocale() {
        return true;
    }
}
