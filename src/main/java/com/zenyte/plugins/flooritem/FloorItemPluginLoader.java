package com.zenyte.plugins.flooritem;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kris | 27. march 2018 : 21:58.44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class FloorItemPluginLoader {
    private static final Logger log = LoggerFactory.getLogger(FloorItemPluginLoader.class);
    public static final Int2ObjectOpenHashMap<FloorItemPlugin> PLUGINS = new Int2ObjectOpenHashMap<>();

    public static void add(final Class<?> c) {
        try {
            if (c.isAnonymousClass()) {
                return;
            }
            if (c.isInterface()) {
                return;
            }
            final Object o = c.newInstance();
            if (!(o instanceof FloorItemPlugin action)) {
                return;
            }
            for (final int item : action.getItems()) {
                PLUGINS.put(item, action);
            }
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
