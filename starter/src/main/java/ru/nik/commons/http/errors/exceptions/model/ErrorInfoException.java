package ru.nik.commons.http.errors.exceptions.model;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import ru.nik.commons.http.errors.ErrorInfo;

public class ErrorInfoException extends RuntimeException {
    private final ErrorInfo errorInfo;

    public ErrorInfoException(ErrorInfo errorInfo) {
        this(errorInfo.getErrorCode(), errorInfo, null);
    }

    public ErrorInfoException(String logMessage, ErrorInfo errorInfo, Throwable cause) {
        super(logMessage, cause);
        Assert.notNull(errorInfo, "errorInfo is null");
        if (errorInfo.getHttpStatus() == null) {
            errorInfo.setHttpStatus(HttpStatus.BAD_REQUEST);
        }
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

}
