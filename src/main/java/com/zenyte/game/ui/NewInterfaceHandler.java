package com.zenyte.game.ui;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

/**
 * @author Kris | 19/10/2018 01:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class NewInterfaceHandler {

    private static final Logger log = LoggerFactory.getLogger(NewInterfaceHandler.class);
    private static final Int2ObjectMap<Interface> interfaces = new Int2ObjectOpenHashMap<>();

    public static Int2ObjectMap<Interface> getInterfaces() {
        return interfaces;
    }

    public static Interface getInterface(int interfaceID) {
        return interfaces.get(interfaceID);
    }

    public static void add(final Class<? extends Interface> clazz) {
        try {
            if (clazz.isInterface()
                    || Modifier.isAbstract(clazz.getModifiers())) return;

            final Interface instance = clazz.getDeclaredConstructor().newInstance();
            instance.attach();
            instance.build();
            interfaces.put(instance.getInterface().getId(), instance);
        } catch (Exception e) {
            log.error("Failed to add interface class " + clazz.getSimpleName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void addUnsafe(final Class<?> clazz) {
        add((Class<? extends Interface>) clazz);
    }

}
