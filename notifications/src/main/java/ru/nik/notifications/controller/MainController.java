package ru.nik.notifications.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.nik.InvestmentsClient;
import ru.nik.model.InvestmentsHelloExt;

@RestController
@RequestMapping("/v1/notifications")
public class MainController {

    private final InvestmentsClient investmentsClient;

    public MainController(InvestmentsClient investmentsClient) {
        this.investmentsClient = investmentsClient;
    }

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello, Notifications!");
    }

    @GetMapping("/investments")
    public Mono<InvestmentsHelloExt> productsHello() {
        return investmentsClient.hello();
    }

}
