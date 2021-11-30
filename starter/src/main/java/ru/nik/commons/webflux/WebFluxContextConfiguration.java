package ru.nik.commons.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.nik.commons.http.errors.ErrorInfoResolver;
import ru.nik.commons.http.errors.InternalErrorInfoResolver;

@Configuration
public class WebFluxContextConfiguration {

    @Bean
    ErrorInfoResolver internalErrorInfoResolver() {
        return new InternalErrorInfoResolver();
    }

}
