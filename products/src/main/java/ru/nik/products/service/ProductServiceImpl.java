package ru.nik.products.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nik.products.model.internal.Product;
import ru.nik.products.repository.ProductRepository;
import ru.nik.products.repository.SomeOtherRepo;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(@Autowired(required = true)  ProductRepository repository, @Autowired(required = true) SomeOtherRepo otherrepo) {
        this.repository = repository;
    }

    @Override @Required
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
