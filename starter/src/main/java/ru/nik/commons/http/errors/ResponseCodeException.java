package ru.nik.commons.http.errors;

/**
 * Exception for {@link ResponseCode}.
 * ResponseCodeException interrupts thread and returns {@link ResponseCode}.
 */
public class ResponseCodeException extends RuntimeException {
    // я ввел данное поле для упрощения проверок исключения в тестах (из какого ResponseCode оно создано)
    private final ResponseCodeError responseCodeError;
    private final ErrorInfo errorInfo;

    public ResponseCodeException(ResponseCodeError responseCodeError, String reason) {
        this(responseCodeError, responseCodeError.getMessage(), reason, null, responseCodeError.isLocale());
    }

    public ResponseCodeException(ResponseCodeError responseCodeError, String errorMessage, String reason) {
        this(responseCodeError, errorMessage, reason, null, false);
    }

    public ResponseCodeException(ResponseCodeError responseCodeError, String errorMessage, String reason, boolean isLocale) {
        this(responseCodeError, errorMessage, reason, null, isLocale);
    }

    public ResponseCodeException(ResponseCodeError responseCodeError, String reason, Throwable throwable) {
        this(responseCodeError, responseCodeError.getMessage(), reason, throwable, responseCodeError.isLocale());
    }

    public ResponseCodeException(ResponseCodeError responseCodeError, String errorMessage, String reason, Throwable throwable, boolean isLocale) {
        super(responseCodeError.getCode() + "; " + reason, throwable);
        this.responseCodeError = responseCodeError;
        this.errorInfo = new ErrorInfo()
                .setErrorCode(responseCodeError.getCode())
                .setMessage(errorMessage)
                .setHttpStatus(responseCodeError.getHttpStatus())
                .setReason(reason)
                .setResolveLocale(isLocale)
                .setLevel(ErrorInfoLevel.FAIL);
    }

    public ResponseCodeException(ResponseCodeError responseCodeError, ErrorInfo errorInfo) {
        super(responseCodeError.getCode() + "; " + errorInfo.getReason());

        this.errorInfo = errorInfo;
        this.responseCodeError = responseCodeError;
    }

    public ResponseCodeError getResponseCode() {
        return this.responseCodeError;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

}
