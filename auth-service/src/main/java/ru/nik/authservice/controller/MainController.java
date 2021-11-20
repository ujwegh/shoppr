package ru.nik.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/auth")
public class MainController {

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello, Auth!");
    }

}
