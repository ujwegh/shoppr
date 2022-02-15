package ru.nik.commons.utils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Interface represents mapper which converts Source S to Target T
 */
public interface Mapper<S, T> {

    T map(S source);

    default Collection<T> mapAll(Collection<S> sources) {
        return sources.stream().map(this::map).collect(Collectors.toList());
    }
}
