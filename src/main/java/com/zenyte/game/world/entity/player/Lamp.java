package com.zenyte.game.world.entity.player;

import com.zenyte.game.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 8-11-2018 | 19:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum Lamp {
    EASY_DIARY_LAMP(30, new Item(13145), 2500),
    MEDIUM_DIARY_LAMP(40, new Item(13146), 7500),
    HARD_DIARY_LAMP(50, new Item(13147), 15000),
    ELITE_DIARY_LAMP(70, new Item(13148), 50000);

    public static final Lamp[] all = values();
    private static final Map<Integer, Lamp> LAMPS = new HashMap<>();

    static {
        for (final Lamp lamp : all) {
            LAMPS.put(lamp.getItem().getId(), lamp);
        }
    }

    private final int minimumLevel;
    private final Item item;
    private final double experience;

    Lamp(final int minimumLevel, final Item item, final double experience) {
        this.minimumLevel = minimumLevel;
        this.item = item;
        this.experience = experience / 5;
    }

    public static Lamp get(final int id) {
        return LAMPS.get(id);
    }

    public int getMinimumLevel() {
        return this.minimumLevel;
    }

    public Item getItem() {
        return this.item;
    }

    public double getExperience() {
        return this.experience;
    }
}
