package ru.nik.authservice.services;

import reactor.core.publisher.Mono;
import ru.nik.authservice.model.LoginRequest;
import ru.nik.authservice.model.LoginResponse;
import ru.nik.authservice.model.SignupRequest;
import ru.nik.authservice.model.SignupResponse;

public interface AuthService {

    Mono<SignupResponse> signup (SignupRequest request);

    Mono<LoginResponse> login (LoginRequest request);

    Mono<String> parseToken (String token);

}
