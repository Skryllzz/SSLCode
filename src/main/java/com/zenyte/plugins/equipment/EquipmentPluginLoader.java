package com.zenyte.plugins.equipment;

import com.zenyte.game.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kris | 25. jaan 2018 : 4:36.14
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class EquipmentPluginLoader {
    private static final Logger log = LoggerFactory.getLogger(EquipmentPluginLoader.class);
    private static final Int2ObjectOpenHashMap<EquipmentPlugin> PLUGINS = new Int2ObjectOpenHashMap<>();
    private static final EquipmentPlugin DEFAULT_PLUGIN;

    static {
        DEFAULT_PLUGIN = new EquipmentPlugin() {
            @Override
            public void handle() {
            }

            @Override
            public int[] getItems() {
                return null;
            }
        };
        DEFAULT_PLUGIN.setDefaultHandlers();
    }

    public static EquipmentPlugin getPlugin(final int key) {
        return Utils.getOrDefault(PLUGINS.get(key), DEFAULT_PLUGIN);
    }

    public static void add(final Class<?> c) {
        try {
            if (c.isAnonymousClass()) {
                return;
            }
            if (c.isInterface()) {
                return;
            }
            final Object o = c.newInstance();
            if (!(o instanceof EquipmentPlugin action)) {
                return;
            }
            action.handle();
            action.setDefaultHandlers();
            for (final int item : action.getItems()) {
                PLUGINS.put(item, action);
            }
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
