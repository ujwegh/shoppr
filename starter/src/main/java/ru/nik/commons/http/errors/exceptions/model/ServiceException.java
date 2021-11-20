package ru.nik.commons.http.errors.exceptions.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceException extends RuntimeException {
    private String errorCode;
    private String message;
}