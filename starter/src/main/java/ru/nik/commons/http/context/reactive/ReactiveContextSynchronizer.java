package ru.nik.commons.http.context.reactive;

import reactor.util.context.Context;
import ru.nik.commons.http.context.threadlocal.ThreadLocalContext;
import ru.nik.commons.http.context.threadlocal.ThreadLocalContextHolder;

public class ReactiveContextSynchronizer {

    /**
     * Позволяет заполнить реактивный контекст из {@link ThreadLocalContext}
     * <p>
     *  Mono.just(o)
     *      .map(o -> doWork(o))
     *      .subscriberContext(ReactiveContextSynchronizer::synchronizeContexts)
     *      .block();
     */

    public static Context synchronizeContexts(Context reactiveContext) {
        return ThreadLocalContextHolder.getContext()
                .map(ThreadLocalContext::getAsMap)
                .map(Context::of)
                .orElse(reactiveContext);
    }
}
