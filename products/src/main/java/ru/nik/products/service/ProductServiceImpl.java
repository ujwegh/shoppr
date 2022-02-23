package ru.nik.products.service;

import ru.nik.products.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nik.products.repository.ProductRepository;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    public final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product getById(Long id) {
        return productRepository.getById(id);
    }

    @Override
    public List<Product> getAllByName(String name) {
        return productRepository.findAll().stream().filter(i -> i.name.contains(name)).toList();
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Long save(Product product) {
        Product p = productRepository.save(product);
        return p.id;
    }

}
