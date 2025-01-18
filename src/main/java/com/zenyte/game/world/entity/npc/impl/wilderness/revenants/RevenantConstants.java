package com.zenyte.game.world.entity.npc.impl.wilderness.revenants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 7 aug. 2018 | 13:21:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum RevenantConstants {
    IMP(7881, 10, 35),
    GOBLIN(7931, 20, 20),
    PYREFIEND(7932, 25, 35),
    HOBGOBLIN(7933, 25, 35),
    CYCLOPS(7934, 50, 38),
    HELLHOUND(7935, 45, 20),
    DEMON(7936, 45, 30),
    ORK(7937, 45, 30),
    DARK_BEAST(7938, 40, 30),
    KNIGHT(7939, 30, 35),
    DRAGON(7940, 40, 30);
    public static final Map<Integer, RevenantConstants> REVENANTS = new HashMap<>();
    private static final RevenantConstants[] VALUES = values();

    static {
        for (final RevenantConstants revenant : VALUES) {
            REVENANTS.put(revenant.getId(), revenant);
        }
    }

    private final int id;
    private final int startHeight;
    private final int delay;

    RevenantConstants(final int id, final int startHeight, final int delay) {
        this.id = id;
        this.startHeight = startHeight;
        this.delay = delay;
    }

    public int getId() {
        return this.id;
    }

    public int getStartHeight() {
        return this.startHeight;
    }

    public int getDelay() {
        return this.delay;
    }
}
