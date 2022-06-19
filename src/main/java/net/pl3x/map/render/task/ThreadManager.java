package net.pl3x.map.render.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.pl3x.map.configuration.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    public static final ThreadManager INSTANCE = new ThreadManager();

    private ExecutorService renderExecutor;
    private ExecutorService saveExecutor;

    private ThreadManager() {
    }

    public ExecutorService getRenderExecutor() {
        if (this.renderExecutor == null) {
            this.renderExecutor = Executors.newFixedThreadPool(Math.max(1, getThreads(Config.RENDER_THREADS)), new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Render-%d").build());
        }
        return this.renderExecutor;
    }

    public ExecutorService getSaveExecutor() {
        if (this.saveExecutor == null) {
            this.saveExecutor = Executors.newFixedThreadPool(Math.max(1, getThreads(Config.IMAGE_THREADS)), new ThreadFactoryBuilder().setNameFormat("Pl3xMap-IO-%d").build());
        }

        return this.saveExecutor;
    }

    public void shutdown() {
        getRenderExecutor().shutdownNow();
        getSaveExecutor().shutdownNow();
    }

    private int getThreads(int threads) {
        if (threads < 1) {
            threads = Runtime.getRuntime().availableProcessors() / 2;
        }
        return threads;
    }
}
