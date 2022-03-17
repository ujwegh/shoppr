package ru.nik.authservice.repositories;

import reactor.core.publisher.Mono;
import ru.nik.authservice.model.User;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByEmail(String email);

}
