package ru.nik.authservice.repositories;

import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.nik.authservice.model.internal.User;

public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByEmail(@Param("email") String email);

}
