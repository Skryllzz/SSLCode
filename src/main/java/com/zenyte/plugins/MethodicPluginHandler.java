package com.zenyte.plugins;

import com.zenyte.processor.Listener;
import com.zenyte.processor.Listener.ListenerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Kris | 18. juuli 2018 : 21:18:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class MethodicPluginHandler {
    private static final Logger log = LoggerFactory.getLogger(MethodicPluginHandler.class);
    private static final Map<ListenerType, List<RegisteredListener>> LISTENERS;

    static {
        final Listener.ListenerType[] values = Listener.ListenerType.values();
        LISTENERS = new EnumMap<>(ListenerType.class);
        for (final Listener.ListenerType value : values) {
            LISTENERS.put(value, new LinkedList<>());
        }
    }


    private static final class RegisteredListener {
        RegisteredListener(final Object instance, final Method method) {
            this.instance = instance;
            this.method = method;
        }

        Object instance;
        Method method;
    }

    public static boolean invokePlugins(final ListenerType type, final Object... params) {
        try {
            for (final MethodicPluginHandler.RegisteredListener listener : LISTENERS.get(type)) {
                listener.method.invoke(listener.instance, params);
            }
            return true;
        } catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("", e);
        }
        return false;
    }

    public static void register(final Class<?> c, final Executable executable) {
        try {
            if (!(executable instanceof Method method)) {
                return;
            }
            final boolean isStatic = Modifier.isStatic(executable.getModifiers());
            final Listener listener = executable.getAnnotation(Listener.class);
            if (listener == null) {
                return;
            }
            method.setAccessible(true);
            final Object instance = (isStatic ? null : c.newInstance());
            LISTENERS.get(listener.type()).add(new RegisteredListener(instance, method));
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
