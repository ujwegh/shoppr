package ru.nik.notifications.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.nik.ProductsClient;
import ru.nik.model.ProductsHelloExt;

@RestController
@RequestMapping("/v1/notifications")
public class MainController {

    private final ProductsClient productsClient;

    public MainController(ProductsClient productsClient) {
        this.productsClient = productsClient;
    }

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello, Notifications!");
    }

    @GetMapping("/products")
    public Mono<ProductsHelloExt> productsHello() {
        return productsClient.hello();
    }

}
