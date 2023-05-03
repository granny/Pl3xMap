/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.scheduler;

public abstract class Task implements Runnable {
    final int delay;
    final boolean repeat;

    boolean cancelled = false;
    int tick;

    /**
     * Creates a new schedulable task.
     *
     * @param delay Delay (in seconds) before task starts
     */
    public Task(int delay) {
        this(delay, false);
    }

    /**
     * Creates a new schedulable task.
     *
     * @param delay  Delay (in seconds) before task starts
     * @param repeat Delay (in seconds) before task repeats
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
