package com.zenyte.game.world.entity.npc.drop.matrix;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kris | 18/11/2018 20:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DropProcessorLoader {
    private static final Logger log = LoggerFactory.getLogger(DropProcessorLoader.class);
    private static final Int2ObjectOpenHashMap<List<DropProcessor>> mappedByNPC = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<List<DropProcessor>> mappedByItem = new Int2ObjectOpenHashMap<>();

    public static List<DropProcessor> get(final int id) {
        return mappedByNPC.get(id);
    }

    public static Int2ObjectOpenHashMap<List<DropProcessor>> getProcessors() {
        return mappedByNPC;
    }

    public static boolean contains(final int itemId) {
        return mappedByItem.containsKey(itemId);
    }

    public static List<DropProcessor> getByItem(final int itemId) {
        return mappedByItem.get(itemId);
    }

    public static void add(final Class<?> c) {
        try {
            if (Modifier.isAbstract(c.getModifiers())) return;
            final Object o = c.newInstance();
            if (!(o instanceof DropProcessor action)) {
                return;
            }
            action.attach();
            for (int key : action.allIds) {
                if (mappedByNPC.containsKey(key)) {
                    mappedByNPC.get(key).add(action);
                } else {
                    mappedByNPC.put(key, new ArrayList<>(Collections.singletonList(action)));
                }
            }
            for (final DropProcessor.DisplayedDrop drop : action.getBasicDrops()) {
                if (mappedByItem.containsKey(drop.getId())) {
                    mappedByItem.get(drop.getId()).add(action);
                } else {
                    mappedByItem.put(drop.getId(), new ArrayList<>(Collections.singletonList(action)));
                }
            }
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
