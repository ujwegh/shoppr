package ru.nik.commons.scheduler;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.NonNull;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.*;

public class ShopprScheduler implements Scheduler, Executor {
    private final Scheduler scheduler;
    private static final String EXECUTOR_NAME = "shopprScheduler";

    public ShopprScheduler(int corePoolSize,
                           int maximumPoolSize,
                           long keepAliveTimeInSeconds,
                           int queueCapacity,
                           MeterRegistry meterRegistry) {
        final ExecutorService executorService =
                new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepAliveTimeInSeconds,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(queueCapacity)
                );

        ExecutorService executorServiceWithMetrics = ExecutorServiceMetrics.monitor(meterRegistry, executorService,
                EXECUTOR_NAME);
        this.scheduler = Schedulers.fromExecutorService(executorServiceWithMetrics, EXECUTOR_NAME);
    }

    @Override
    public Disposable schedule(Runnable task) {
        return scheduler.schedule(task);
    }

    @Override
    public Worker createWorker() {
        return scheduler.createWorker();
    }

    @Override
    public Disposable schedule(Runnable task, long delay, TimeUnit unit) {
        return scheduler.schedule(task, delay, unit);
    }

    @Override
    public Disposable schedulePeriodically(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return scheduler.schedulePeriodically(task, initialDelay, period, unit);
    }

    @Override
    public long now(TimeUnit unit) {
        return scheduler.now(unit);
    }

    @Override
    public void dispose() {
        scheduler.dispose();
    }

    @Override
    public void start() {
        scheduler.start();
    }

    @Override
    public boolean isDisposed() {
        return scheduler.isDisposed();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        scheduler.schedule(command);
    }
}
