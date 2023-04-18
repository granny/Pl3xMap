package net.pl3x.map.core.scheduler;

public abstract class Task implements Runnable {
    final int delay;
    final boolean repeat;

    boolean cancelled = false;
    int tick;

    /**
     * Creates a new schedulable task.
     *
     * @param delay Delay (in ticks) before task starts
     */
    public Task(int delay) {
        this(delay, false);
    }

    /**
     * Creates a new schedulable task.
     *
     * @param delay  Delay (in ticks) before task starts
     * @param repeat Delay (in ticks) before task repeats
     */
    public Task(int delay, boolean repeat) {
        this.delay = delay;
        this.repeat = repeat;
    }

    /**
     * Mark task as cancelled.
     */
    public void cancel() {
        this.cancelled = true;
    }

    /**
     * Check if task is marked as cancelled.
     *
     * @return True if cancelled
     */
    public boolean cancelled() {
        return this.cancelled;
    }
}
