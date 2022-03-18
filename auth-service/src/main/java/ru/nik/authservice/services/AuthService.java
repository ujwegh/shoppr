package ru.nik.authservice.services;

import reactor.core.publisher.Mono;
import ru.nik.authservice.model.external.LoginRequest;
import ru.nik.authservice.model.external.LoginResponse;
import ru.nik.authservice.model.external.SignupRequest;
import ru.nik.authservice.model.external.SignupResponse;

public interface AuthService {

    Mono<SignupResponse> signup (SignupRequest request);

    Mono<LoginResponse> login (LoginRequest request);

    Mono<String> parseToken (String token);

}
