package ru.nik.authservice.web;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.nik.authservice.services.AuthService;

import java.util.List;

@Component
class AuthFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final AuthService authService;

    AuthFunction(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest request,
                                       HandlerFunction<ServerResponse> handlerFunction) {
        List<String> headers = request.headers().header("Authorization");
        if (headers.isEmpty()) return ServerResponse.status(403).build();

        String token = headers.get(0);

        return authService.parseToken(token)
                .flatMap(res -> handlerFunction.handle(request))
                .onErrorResume(err -> ServerResponse.status(403).build());
    }
}
