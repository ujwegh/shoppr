package ru.nik.notifications.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import ru.nik.commons.http.errors.ErrorInfoErrorAttributes;
import ru.nik.commons.http.errors.ErrorInfoResolver;
import ru.nik.commons.http.errors.ErrorInfoWebExceptionHandler;
import ru.nik.commons.http.errors.exceptions.mapper.ExceptionMapper;
import ru.nik.commons.http.logging.LoggingWebFilter;

import java.util.List;

@Configuration
public class ErrorContextConfiguration {
    @Bean
    ErrorInfoErrorAttributes errorAttributes(@Autowired(required = false) java.util.List<ExceptionMapper> mappers) {
        return new ErrorInfoErrorAttributes(mappers);
    }

    @Bean
    ErrorInfoWebExceptionHandler webExceptionHandler(ErrorAttributes errorAttributes,
                                                     ServerCodecConfigurer serverCodecConfigurer,
                                                     ErrorInfoResolver internalErrorInfoResolver) {
        return new ErrorInfoWebExceptionHandler(
                errorAttributes,
                serverCodecConfigurer.getReaders(),
                serverCodecConfigurer.getWriters(),
                internalErrorInfoResolver
        );
    }


    @Bean
    LoggingWebFilter loggingWebFilter(MeterRegistry meterRegistry) {
        LoggingWebFilter loggingWebFilter = new LoggingWebFilter(meterRegistry);
        loggingWebFilter.setExcludedFromLog(List.of(
                "/actuator/health",
                "/actuator/prometheus"
        ));
        loggingWebFilter.setLogHeaders(true);
        return loggingWebFilter;
    }


}
