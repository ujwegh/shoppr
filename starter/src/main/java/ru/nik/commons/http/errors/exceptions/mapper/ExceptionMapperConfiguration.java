package ru.nik.commons.http.errors.exceptions.mapper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionMapperConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "notFoundExceptionMapper")
    ExceptionMapper notFoundExceptionMapper() {
        return new NotFoundExceptionMapper();
    }

    @Bean
    @ConditionalOnMissingBean(name = "responseCodeExceptionMapper")
    ExceptionMapper responseCodeExceptionMapper() {
        return new ResponseCodeExceptionMapper();
    }

    @Bean
    @ConditionalOnMissingBean(name = "errorInfoExceptionMapper")
    ExceptionMapper errorInfoExceptionMapper() {
        return new ErrorInfoExceptionMapper();
    }
}
