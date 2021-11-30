package ru.nik.products.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.errors.ResponseCodeException;
import ru.nik.products.exceptions.ExampleErrorException;
import ru.nik.products.model.ProductsHello;

@RestController
@RequestMapping("/v1/products")
public class MainController {

    @GetMapping("/hello")
    public Mono<ProductsHello> hello() {
        ProductsHello productsHello = new ProductsHello();
        productsHello.setValue("Hello, Products!");
        return Mono.just(productsHello);
//        return Mono.error(new ResponseCodeException(new ExampleErrorException(), "something has been broken"));
    }

}
