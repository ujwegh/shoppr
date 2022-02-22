package ru.nik.products.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.nik.products.model.internal.Product;

public interface InvestmentsRepository extends ReactiveMongoRepository<Product, String> {
}
