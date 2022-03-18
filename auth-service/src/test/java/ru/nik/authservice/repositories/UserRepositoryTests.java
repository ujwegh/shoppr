package ru.nik.authservice.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserRepositoryTests {

    @Autowired
    UserRepository repository;

    @Test
    void readsAllEntitiesCorrectly() {

        repository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void readsEntitiesByNameCorrectly() {

        repository.findByEmail("testmail")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
}
