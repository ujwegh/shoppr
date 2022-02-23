package ru.nik.investments.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.nik.investments.model.internal.Product;

public interface InvestmentsRepository extends ReactiveMongoRepository<Product, String> {
}
