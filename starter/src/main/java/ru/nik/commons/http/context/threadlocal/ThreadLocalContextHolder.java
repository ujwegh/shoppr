package ru.nik.commons.http.context.threadlocal;

import reactor.util.context.ContextView;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Класс хранит в себе контекст в рамках потока.
 * */
public class ThreadLocalContextHolder {

    private static final ThreadLocal<ThreadLocalContext> context = new ThreadLocal<>();

    public static void fillContext(ContextView reactiveContext) {
        Map<Object, Object> contextMap = reactiveContext.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        context.set(new ThreadLocalContext(contextMap));
    }

    public static Optional<ThreadLocalContext> getContext() {
        return Optional.ofNullable(context.get());
    }

    public static void clearContext() {
        Optional.ofNullable(context.get())
                .ifPresent(ThreadLocalContext::clear);
    }
}
