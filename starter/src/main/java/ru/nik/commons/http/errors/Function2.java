package ru.nik.commons.http.errors;

public interface Function2<A, B, R> {
    R map(A response, B body);
}
