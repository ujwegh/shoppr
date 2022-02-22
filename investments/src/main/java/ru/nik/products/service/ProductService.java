package ru.nik.products.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nik.products.model.internal.Product;

public interface ProductService {
    Mono<Product> findByUuid(String uuid);

    Mono<Product> create(Product product);

    Mono<Void> deleteByUuid(String uuid);

    Flux<Product> findAll();
}
