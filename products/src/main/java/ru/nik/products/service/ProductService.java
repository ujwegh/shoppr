package ru.nik.products.service;

import ru.nik.products.model.Product;

import java.util.List;

public interface ProductService {

    public List<Product> getAll();

    public Product getById(Long id);

    public List<Product> getAllByName(String name);

    public void deleteById(Long id);

    public Long save(Product product);

}
