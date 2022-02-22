package ru.nik.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class InvestmentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestmentsApplication.class, args);
    }

}
