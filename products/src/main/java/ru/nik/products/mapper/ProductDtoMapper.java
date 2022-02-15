package ru.nik.products.mapper;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.nik.commons.utils.MonoMapper;
import ru.nik.products.model.external.ProductDto;
import ru.nik.products.model.internal.Product;

@Component
public class ProductDtoMapper implements MonoMapper<Product, ProductDto> {
    @Override
    public Mono<ProductDto> map(Product source) {
        return Mono.just(new ProductDto()
                .setUuid(source.getUuid())
                .setTitle(source.getTitle())
                .setValue(source.getValue())
                .setPrice(source.getPrice().doubleValue()));
    }
}
