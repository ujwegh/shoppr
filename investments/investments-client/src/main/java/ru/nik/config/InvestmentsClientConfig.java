package ru.nik.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.nik.InvestmentsClient;
import ru.nik.commons.http.internal.InternalRequestExecutor;
import ru.nik.commons.http.internal.InternalRequestExecutors;
import ru.nik.commons.http.mapper.ObjectJsonMapper;

@Configuration
public class InvestmentsClientConfig {
    public static final String SERVICE_NAME = "INVESTMENTS";
    public static final String URL_PROP = "investments.service.url";

    @Bean
    public InternalRequestExecutor investmentsInternalRequestExecutor(InternalRequestExecutors internalRequestExecutors) {
        return internalRequestExecutors.get(SERVICE_NAME, URL_PROP);
    }

    @Bean
    public InvestmentsClient investmentsClient(@Qualifier("investmentsInternalRequestExecutor") InternalRequestExecutor internalRequestExecutor,
                                       ObjectJsonMapper objectJsonMapper) {
        return new InvestmentsClient(internalRequestExecutor, objectJsonMapper);
    }
}
