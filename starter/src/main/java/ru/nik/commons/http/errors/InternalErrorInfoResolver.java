package ru.nik.commons.http.errors;

import reactor.core.publisher.Mono;

public class InternalErrorInfoResolver implements ErrorInfoResolver {
    @Override
    public Mono<String> resolveError(String errorKey) {
        return Mono.just(errorKey);
    }
}
