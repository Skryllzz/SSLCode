package mgi.types.config.identitykit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 13-2-2019 | 16:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum BeardStyle {
    CLEAN_SHAVEN(0, 14),
    GOATEE(1, 10),
    LONG(2, 11),
    MEDIUM(3, 12),
    SMALL_MOUSTACHE(4, 13),
    SHORT(5, 15),
    POINTY(6, 16),
    SPLIT(7, 17),
    HANDLEBAR(8, 111),
    MUTTON(9, 112),
    FULL_MOTTON(10, 113),
    BIG_MOUSTACHE(11, 114),
    WAXED_MOUSTACHE(12, 115),
    DALI(13, 116),
    VIZIER(14, 117);
    private final int slotId;
    private final int id;
    private static final BeardStyle[] VALUES = values();
    private static final Map<Integer, Integer> STYLES = new HashMap<>(VALUES.length);

    static {
        for (final BeardStyle style : VALUES) {
            STYLES.put(style.slotId, style.id);
        }
    }

    public static int getStyle(final int slotId) {
        return STYLES.get(slotId);
    }

    public int getSlotId() {
        return this.slotId;
    }

    public int getId() {
        return this.id;
    }

    BeardStyle(final int slotId, final int id) {
        this.slotId = slotId;
        this.id = id;
    }
}
