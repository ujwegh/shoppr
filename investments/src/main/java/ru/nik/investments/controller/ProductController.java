package ru.nik.investments.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.context.threadlocal.ThreadLocalContextWrapper;
import ru.nik.commons.utils.MonoMapper;
import ru.nik.investments.model.external.ProductDto;
import ru.nik.investments.model.external.ProductsHello;
import ru.nik.investments.model.internal.Product;
import ru.nik.investments.service.ProductService;

@RestController
@RequestMapping("/v1/investments")
public class ProductController {

    private final ProductService productService;
    private final MonoMapper<Product, ProductDto> productDtoMapper;
    private final MonoMapper<ProductDto, Product> productMapper;

    public ProductController(ProductService productService,
                             MonoMapper<Product, ProductDto> productDtoMapper,
                             MonoMapper<ProductDto, Product> productMapper) {
        this.productService = productService;
        this.productDtoMapper = productDtoMapper;
        this.productMapper = productMapper;
    }

    @GetMapping("/hello")
    public Mono<ProductsHello> hello() {
        ProductsHello productsHello = new ProductsHello();
        productsHello.setValue("Hello, Investments!");
        return Mono.just(productsHello);
//        return Mono.error(new ResponseCodeException(new ExampleErrorException(), "something has been broken"));
    }

    @PostMapping
    public Mono<ProductDto> create(@RequestBody ProductDto productDto) {
        return productMapper.map(productDto)
                .flatMap(productService::create)
                .flatMap(productDtoMapper::map);
    }

    @GetMapping("/{uuid}")
    public Mono<ProductDto> findByUuid(@PathVariable("uuid") @RequestBody String uuid) {
        return productService.findByUuid(uuid).flatMap(productDtoMapper::map);
    }


}
