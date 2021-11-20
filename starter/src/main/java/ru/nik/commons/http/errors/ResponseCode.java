package ru.nik.commons.http.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ru.nik.commons.http.errors.exceptions.mapper.ExceptionMapper;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ResponseCode implements ResponseCodeError {
    SYSTEM_ERROR("SYSTEM_ERROR", ExceptionMapper.DEFAULT_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, true)
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
    private final boolean isLocale;

    public static Optional<ResponseCode> fromCode(String code) {
        for (ResponseCode responseCode : values()) {
            if (responseCode.code.equalsIgnoreCase(code)) {
                return Optional.of(responseCode);
            }
        }

        return Optional.empty();
    }
}
