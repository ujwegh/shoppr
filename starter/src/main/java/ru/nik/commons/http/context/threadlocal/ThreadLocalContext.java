package ru.nik.commons.http.context.threadlocal;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EqualsAndHashCode
@ToString
public class ThreadLocalContext {
    private final Map<Object, Object> contextMap;

    public ThreadLocalContext() {
        this.contextMap = new ConcurrentHashMap<>();
    }

    public ThreadLocalContext(Map<Object, Object> contextMap) {
        this.contextMap = new ConcurrentHashMap<>(contextMap);
    }

    public Object get(Object key) {
        return this.contextMap.get(key);
    }

    public Map<Object, Object> getAsMap() {
        return Collections.unmodifiableMap(contextMap);
    }

    public void put(Object key, Object value) {
        this.contextMap.put(key, value);
    }

    public void clear() {
        this.contextMap.clear();
    }
}
