package ru.nik.commons.http.context.threadlocal;

import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.function.Supplier;

public class ThreadLocalContextWrapper {

    private final Scheduler scheduler;

    public ThreadLocalContextWrapper(@Qualifier("ShopprScheduler") Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Данный метод позволяет обернуть вызов в {@link ThreadLocalContext}.
     * {@link ThreadLocalContext} заполняется данными из {@link Context}
     * <p>
     * private Mono<ServerResponse> scanQr(ServerRequest request) {
     * return request.bodyToMono(QrRequestExt.class)
     * .flatMap(qrRequestExt -> contextWrapper.wrap(() -> doWork(qrRequestExt)))
     * .flatMap(RouterUtils::buildServerResponseWithStatusOk);
     * }
     */
    public <T> Mono<T> wrap(Supplier<T> supplier) {
        return Mono.deferContextual(Mono::just)
                .map(context -> wrapWithThreadLocalContext(supplier, context))
                .subscribeOn(scheduler);
    }

    /**
     * Данный метод позволяет обернуть вызов в {@link ThreadLocalContext}.
     * {@link ThreadLocalContext} заполняется данными из {@link Context}
     * <p>
     * private Mono<ServerResponse> scanQr(ServerRequest request) {
     * return request.bodyToMono(QrRequestExt.class)
     * .flatMap(qrRequestExt -> contextWrapper.wrap(() -> doWork(qrRequestExt)))
     * .flatMap(RouterUtils::buildServerResponseWithStatusOk);
     * }
     */
    public Mono<Void> wrap(Runnable runnable) {
        return Mono.deferContextual(Mono::just)
                .map(context -> {
                    wrapWithThreadLocalContext(runnable, context);
                    return new Object();
                })
                .then()
                .subscribeOn(scheduler);
    }

    private <T> T wrapWithThreadLocalContext(Supplier<T> supplier, ContextView context) {
        try {
            ThreadLocalContextHolder.fillContext(context);
            return supplier.get();
        } finally {
            ThreadLocalContextHolder.clearContext();
        }
    }

    private void wrapWithThreadLocalContext(Runnable runnable, ContextView context) {
        try {
            ThreadLocalContextHolder.fillContext(context);
            runnable.run();
        } finally {
            ThreadLocalContextHolder.clearContext();
        }
    }
}
