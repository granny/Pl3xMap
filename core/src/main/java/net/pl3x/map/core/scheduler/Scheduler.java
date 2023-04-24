package net.pl3x.map.core.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Scheduler {
    private final List<@NonNull Task> tasks = new ArrayList<>();

    /**
     * Tick this scheduler.
     */
    public void tick() {
        Iterator<Task> iter = this.tasks.iterator();
        while (iter.hasNext()) {
            Task task = iter.next();
            if (task.tick++ < task.delay) {
                continue;
            }
            if (task.cancelled()) {
                iter.remove();
                continue;
            }
            task.run();
            if (task.repeat) {
                task.tick = 0;
                continue;
            }
            iter.remove();
        }
    }

    /**
     * Cancel all scheduled tasks.
     */
    public void cancelAll() {
        Iterator<Task> iter = this.tasks.iterator();
        while (iter.hasNext()) {
            iter.next().cancel();
            iter.remove();
        }
    }

    /**
     * Add task to the scheduler.
     *
     * @param task Task to add
     */
    public void addTask(@NonNull Task task) {
        this.tasks.add(task);
    }

    /**
     * Add task to the scheduler.
     *
     * @param delay    Delay (in ticks) before task starts
     * @param runnable Task to add
     */
    public void addTask(int delay, @NonNull Runnable runnable) {
        addTask(delay, false, runnable);
    }

    /**
     * Add task to the scheduler.
     *
     * @param delay    Delay (in ticks) before task starts
     * @param repeat   Delay (in ticks) before task repeats
     * @param runnable Task to add
     */
    public void addTask(int delay, boolean repeat, @NonNull Runnable runnable) {
        addTask(new Task(delay, repeat) {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }
}
