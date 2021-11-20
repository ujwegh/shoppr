package ru.nik.commons.http.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDescription {
    private final String uri;
    private final String message;
    private final String cause;
    private final String traceId;
}
