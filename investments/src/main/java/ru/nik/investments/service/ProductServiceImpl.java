package ru.nik.investments.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nik.investments.model.internal.Product;
import ru.nik.investments.repository.InvestmentsRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final InvestmentsRepository repository;

    public ProductServiceImpl(@Autowired InvestmentsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Product> findByUuid(String uuid) {
        return repository.findById(uuid);
    }

    @Override
    public Mono<Product> create(Product product) {
        return repository.save(product);
    }

    @Override
    public Mono<Void> deleteByUuid(String uuid) {
        return repository.deleteById(uuid);
    }

    @Override
    public Flux<Product> findAll() {
        return repository.findAll();
    }
}
