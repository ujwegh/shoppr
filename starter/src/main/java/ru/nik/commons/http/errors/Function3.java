package ru.nik.commons.http.errors;

public interface Function3<A, B, C, R> {
    R map(A response, B body, C path);
}
