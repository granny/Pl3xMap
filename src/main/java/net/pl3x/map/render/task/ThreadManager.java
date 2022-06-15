package net.pl3x.map.render.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    public static final ThreadManager INSTANCE = new ThreadManager();

    private ExecutorService renderExecutor;

    private ThreadManager() {
    }

    public ExecutorService getRenderExecutor() {
        if (this.renderExecutor == null) {
            this.renderExecutor = Executors.newFixedThreadPool(Math.max(1, getThreads()), new ThreadFactoryBuilder().setNameFormat("Render-%d").build());
        }
        return this.renderExecutor;
    }

    public void runAsync(Runnable task, ExecutorService executor) {
        runAsync(task, null, executor);
    }

    public void runAsync(Runnable task, Runnable whenComplete, ExecutorService executor) {
        CompletableFuture.runAsync(task, executor)
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                })
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                    if (whenComplete != null) {
                        whenComplete.run();
                    }
                });
    }

    private int getThreads() {
        int threads = 0; // TODO - make configurable
        if (threads < 1) {
            threads = Runtime.getRuntime().availableProcessors() / 3;
        }
        return threads;
    }
}
