package ru.nik.commons.http.errors;

import reactor.core.publisher.Mono;

public interface ErrorInfoResolver {

    Mono<String> resolveError(String errorKey);

}
