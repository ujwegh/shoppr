package ru.nik.commons.http.errors.exceptions.model;

import org.springframework.http.HttpStatus;
import ru.nik.commons.http.errors.ErrorInfo;
import ru.nik.commons.http.errors.ResponseCode;


public class SystemException extends RuntimeException {
    private final ErrorInfo errorInfo;

    public SystemException(String reason) {
        this(reason, null);
    }

    public SystemException(String reason, Throwable cause) {
        super(ResponseCode.SYSTEM_ERROR.getCode() + ": " + reason, cause);
        this.errorInfo = new ErrorInfo()
                // наружу ничего не сообщаем - всегда один и тот же SYSTEM_ERROR
                .setErrorCode(ResponseCode.SYSTEM_ERROR.getCode())
                .setMessage(ResponseCode.SYSTEM_ERROR.getMessage())
                .setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .setReason(reason);
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

}
