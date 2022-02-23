package ru.nik.investments.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nik.investments.model.internal.Product;

public interface ProductService {
    Mono<Product> findByUuid(String uuid);

    Mono<Product> create(Product product);

    Mono<Void> deleteByUuid(String uuid);

    Flux<Product> findAll();
}
