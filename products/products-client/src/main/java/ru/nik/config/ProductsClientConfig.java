package ru.nik.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.nik.ProductsClient;
import ru.nik.commons.http.internal.InternalRequestExecutor;
import ru.nik.commons.http.internal.InternalRequestExecutors;
import ru.nik.commons.http.mapper.ObjectJsonMapper;

@Configuration
public class ProductsClientConfig {
    public static final String SERVICE_NAME = "PRODUCTS";
    public static final String URL_PROP = "products.service.url";

    @Bean
    public InternalRequestExecutor productsInternalRequestExecutor(InternalRequestExecutors internalRequestExecutors) {
        return internalRequestExecutors.get(SERVICE_NAME, URL_PROP);
    }

    @Bean
    public ProductsClient otpClient(@Qualifier("productsInternalRequestExecutor") InternalRequestExecutor internalRequestExecutor,
                                    ObjectJsonMapper objectJsonMapper) {
        return new ProductsClient(internalRequestExecutor, objectJsonMapper);
    }
}
