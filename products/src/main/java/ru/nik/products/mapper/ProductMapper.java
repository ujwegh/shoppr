package ru.nik.products.mapper;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.nik.commons.utils.MonoMapper;
import ru.nik.products.model.external.ProductDto;
import ru.nik.products.model.internal.Product;

import java.math.BigDecimal;

@Component
public class ProductMapper implements MonoMapper<ProductDto, Product> {
    @Override
    public Mono<Product> map(ProductDto source) {
        return Mono.just(new Product()
                .setUuid(source.getUuid())
                .setTitle(source.getTitle())
                .setValue(source.getValue())
                .setPrice(BigDecimal.valueOf(source.getPrice())));
    }
}
