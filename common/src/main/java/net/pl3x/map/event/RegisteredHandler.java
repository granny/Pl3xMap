package net.pl3x.map.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.pl3x.map.addon.Addon;
import org.jetbrains.annotations.NotNull;

public class RegisteredHandler {
    private final Addon addon;
    private final EventListener listener;
    private final Method method;

    public RegisteredHandler(@NotNull Addon addon, @NotNull EventListener listener, @NotNull Method method) {
        this.addon = addon;
        this.listener = listener;
        this.method = method;
    }

    @NotNull
    public Addon getAddon() {
        return this.addon;
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
        getMethod().invoke(getListener(), event);
    }

    @Override
    @NotNull
    public String toString() {
        return "Handler{addon=" + getAddon().getName() +
                ",listener=" + getListener().getClass().getName() +
                ",method=" + getMethod().getName() + "}";
    }
}
