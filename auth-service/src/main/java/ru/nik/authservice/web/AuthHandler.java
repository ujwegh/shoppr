package ru.nik.authservice.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.nik.authservice.errors.LoginDeniedException;
import ru.nik.authservice.model.LoginRequest;
import ru.nik.authservice.model.LoginResponse;
import ru.nik.authservice.model.SignupRequest;
import ru.nik.authservice.model.SignupResponse;
import ru.nik.authservice.services.AuthService;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthService service;

    public Mono<ServerResponse> signup (ServerRequest request){
        Mono<SignupRequest> body = request.bodyToMono(SignupRequest.class);
        Mono<SignupResponse> result = body.flatMap(service::signup);
        return result
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> login (ServerRequest request){
        Mono<LoginRequest> body = request.bodyToMono(LoginRequest.class);
        Mono<LoginResponse> result = body.flatMap(service::login);
        return result
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(data))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> {
                    if (error instanceof LoginDeniedException){
                        return ServerResponse.badRequest().build();
                    }
                    return ServerResponse.status(500).build();
                });
    }
}
