package ru.nik.commons.http.mapper;

import com.fasterxml.jackson.core.type.TypeReference;

public interface ObjectJsonMapper {

    <T> String toJson(T object);

    <T> T toObject(String jsonAsString, Class<T> clazz);

    <T> T toObject(String jsonAsString, TypeReference<T> valueTypeRef);
}
