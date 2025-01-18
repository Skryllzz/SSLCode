package com.zenyte.game.content;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 19 aug. 2018 | 20:29:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public enum MaxCape {
    ARDOUGNE(20760, 20764, 13124),
    ASSEMBLER(21898, 21900, 22109),
    ACCUMULATOR(13337, 13338, 10499),
    INFERNAL(21285, 21282, 21295),
    FIRE(13329, 13330, 6570),
    SARADOMIN(13331, 13332, 2412),
    ZAMORAK(13333, 13334, 2414),
    GUTHIX(13335, 13336, 2413),
    IMBUED_SARADMIN(21776, 21778, 21791),
    IMBUED_ZAMORAK(21780, 21782, 21795),
    IMBUED_GUTHIX(21784, 21786, 21793);
    public static final MaxCape[] values = values();
    private static final Map<Integer, MaxCape> CAPES = new HashMap<>(values.length);

    static {
        for (final MaxCape cape : values) {
            CAPES.put(cape.getUpgrade(), cape);
        }
    }

    private final int cape;
    private final int hood;
    private final int upgrade;

    MaxCape(final int cape, final int hood, final int upgrade) {
        this.cape = cape;
        this.hood = hood;
        this.upgrade = upgrade;
    }

    public static MaxCape get(final int value) {
        return CAPES.get(value);
    }

    public int getCape() {
        return this.cape;
    }

    public int getHood() {
        return this.hood;
    }

    public int getUpgrade() {
        return this.upgrade;
    }
}
