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
package net.pl3x.map.core.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class RegisteredHandler {
    private final EventListener listener;
    private final Method method;

    public RegisteredHandler(@NotNull EventListener listener, @NotNull Method method) {
        this.listener = listener;
        this.method = method;
    }

    @NotNull
    public EventListener getListener() {
        return this.listener;
    }

    @NotNull
    public Method getMethod() {
        return this.method;
    }

    public void execute(@NotNull Event event) throws InvocationTargetException, IllegalAccessException {
        getMethod().setAccessible(true);
        getMethod().invoke(getListener(), event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        RegisteredHandler other = (RegisteredHandler) o;
        return Objects.equals(getListener(), other.getListener())
                && Objects.equals(getMethod(), other.getMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getListener(), getMethod());
    }

    @Override
    @NotNull
    public String toString() {
        return "Handler{" +
                "listener=" + getListener().getClass().getName() +
                ",method=" + getMethod().getName() + "}";
    }
}
