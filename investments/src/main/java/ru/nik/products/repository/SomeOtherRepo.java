package ru.nik.products.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.nik.products.model.internal.Product;

public interface SomeOtherRepo  extends ReactiveMongoRepository<Product, String> {
}
