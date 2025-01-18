package com.zenyte.plugins;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Set;

/**
 * @author Kris | 21/03/2019 16:16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PluginManager {
    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
    /**
     * A map of executables, wherein the key is the event class, and the value is a set of subscribed methods.
     */
    private static final Map<Class<?>, Set<Method>> executableMap = new Object2ObjectOpenHashMap<>();

    /**
     * Regosters an executable method.
     *
     * @param superClass the class in which the subscribed event is.
     * @param executable the executable method subscribed.
     */
    public static void register(@NotNull final Class<?> superClass, @NotNull final Method executable) {
        //Do not register the event if the method isn't declared by the class publishing it. Static methods are still inherited through extensions, so we filter all those.
        if (!ArrayUtils.contains(superClass.getDeclaredMethods(), executable)) {
            return;
        }
        Preconditions.checkArgument(Modifier.isStatic(executable.getModifiers()), "Registered events must be static: " + (superClass.getSimpleName() + "::" + executable.getName()));
        Preconditions.checkArgument(executable.isAnnotationPresent(Subscribe.class), "Registered event must have Subscribe annotation preset: " + (superClass.getSimpleName() + "::" + executable.getName()));
        Preconditions.checkArgument(executable.getParameterCount() == 1, "Registered events must only contain one parameter: " + (superClass.getSimpleName() + "::" + executable.getName()));
        final Parameter event = executable.getParameters()[0];
        final Class<?> type = event.getType();
        //log.info("Registering: [" + type.getSimpleName() + "] " + superClass.getSimpleName() + "::" + executable.getName());
        Preconditions.checkArgument(Event.class.isAssignableFrom(type), "Subscribed method's parameter must implement the Event interface.");
        executableMap.computeIfAbsent(type, r -> new ObjectLinkedOpenHashSet<>()).add(executable);
    }

    /**
     * Publishes an event to the listener.
     *
     * @param event the event published.
     */
    public static void post(@NotNull final Event event) {
        final Class<? extends Event> clazz = event.getClass();
        final Set<Method> executables = executableMap.get(clazz);
        if (executables == null) {
            return;
        }
        for (final Method executable : executables) {
            try {
                executable.invoke(null, event);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
}
