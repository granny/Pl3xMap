package net.pl3x.map.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    @NotNull
    public String toString() {
        return "Handler{" +
                ",listener=" + getListener().getClass().getName() +
                ",method=" + getMethod().getName() + "}";
    }
}
