package ru.nik.commons.http.errors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * Error object that client will see
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorInfo {
    @JsonIgnore
    private long timestamp = System.currentTimeMillis();
    @JsonIgnore
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private String errorCode;
    private String message;
    @JsonIgnore
    private String reason;
    @JsonIgnore
    private boolean resolveLocale;
    private ErrorInfoLevel level = ErrorInfoLevel.FAIL;
    private ErrorDescription description;
}
