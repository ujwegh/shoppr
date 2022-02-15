package ru.nik.commons.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Interface represents mapper which converts Source S to Target Mono<T>
 */
public interface MonoMapper<S, T> {
    Mono<T> map(S source);

    default Flux<T> mapAll(Collection<S> sources) {
        return Flux.fromIterable(sources)
                .flatMap(this::map);
    }
}
