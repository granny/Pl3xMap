package net.pl3x.map.event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.logger.Logger;
import org.jetbrains.annotations.NotNull;

public class EventRegistry {
    public void callEvent(@NotNull Event event) {
        for (RegisteredHandler handler : event.getHandlers()) {
            try {
                Logger.debug("Executing Event " + handler);
                handler.execute(event);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void register(@NotNull EventListener listener, @NotNull Addon addon) {
        for (Method method : listener.getClass().getMethods()) {
            if (method.getDeclaredAnnotation(EventHandler.class) == null) {
                continue;
            }
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 0) {
                Logger.warn(String.format("Annotated EventListener for %s does not have an Event param %s@%s",
                        addon.getName(), listener.getClass(), method.getName()));
                continue;
            }
            Class<?> event = params[0];
            if (!Event.class.isAssignableFrom(event)) {
                Logger.warn(String.format("%s is not an event", event.getName()));
                continue;
            }
            try {
                Field handlers = event.getDeclaredField("handlers");
                handlers.setAccessible(true);

                @SuppressWarnings("unchecked")
                List<RegisteredHandler> list = (List<RegisteredHandler>) handlers.get(event);

                RegisteredHandler handler = new RegisteredHandler(addon, listener, method);
                list.add(handler);

                Logger.debug("Registered Event " + handler);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
