package ru.nik.commons.webflux;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import ru.nik.commons.http.errors.ErrorInfoErrorAttributes;
import ru.nik.commons.http.errors.ErrorInfoResolver;
import ru.nik.commons.http.errors.ErrorInfoWebExceptionHandler;
import ru.nik.commons.http.errors.InternalErrorInfoResolver;
import ru.nik.commons.http.errors.exceptions.mapper.ExceptionMapper;
import ru.nik.commons.http.logging.LoggingWebFilter;

import java.util.Arrays;

@Configuration
public class WebFluxContextConfiguration {

//    @Bean
//    @ConditionalOnMissingBean(LoggingWebFilter.class)
//    @ConditionalOnMissingClass("ru.nik.commons.http.logging.LoggingWebFilter")
//    LoggingWebFilter loggingWebFilter(MeterRegistry meterRegistry) {
//        LoggingWebFilter loggingWebFilter = new LoggingWebFilter(meterRegistry);
//        loggingWebFilter.setExcludedFromLog(Arrays.asList(
//                "/actuator/health",
//                "/actuator/prometheus"
//        ));
//        loggingWebFilter.setLogHeaders(true);
//        return loggingWebFilter;
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(ErrorInfoWebExceptionHandler.class)
//    ErrorInfoWebExceptionHandler webExceptionHandler(ErrorAttributes errorAttributes,
//                                                     ServerCodecConfigurer serverCodecConfigurer,
//                                                     ErrorInfoResolver errorInfoResolver) {
//        return new ErrorInfoWebExceptionHandler(
//                errorAttributes,
//                serverCodecConfigurer.getReaders(),
//                serverCodecConfigurer.getWriters(),
//                errorInfoResolver);
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(name = "errorAttributes")
//    ErrorInfoErrorAttributes errorAttributes(@Autowired(required = false) java.util.List<ExceptionMapper> mappers) {
//        return new ErrorInfoErrorAttributes(mappers);
//    }
//
    @Bean
    ErrorInfoResolver internalErrorInfoResolver() {
        return new InternalErrorInfoResolver();
    }

}
