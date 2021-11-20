package ru.nik.commons.http.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class ObjectJsonMapperImpl implements ObjectJsonMapper {

    private final ObjectMapper objectMapper;

    public ObjectJsonMapperImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @SneakyThrows
    public <T> String toJson(T object) {
        return this.objectMapper.writeValueAsString(object);
    }

    @Override
    @SneakyThrows
    public <T> T toObject(String jsonAsString, Class<T> clazz) {
        return this.objectMapper.readValue(jsonAsString, clazz);
    }

    @Override
    @SneakyThrows
    public <T> T toObject(String jsonAsString, TypeReference<T> valueTypeRef) {
        return objectMapper.readValue(jsonAsString, valueTypeRef);
    }
}
