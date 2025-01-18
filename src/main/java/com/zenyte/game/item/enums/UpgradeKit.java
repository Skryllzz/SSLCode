package com.zenyte.game.item.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author Kris | 07/05/2019 20:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum UpgradeKit {
    STEAM_STAFF(12795, 12798, 11787),
    MYSTIC_STEAM_STAFF(12796, 12798, 11789),
    LAVA_STAFF(21198, 21202, 3053),
    MYSTIC_LAVA_STAFF(21200, 21202, 3054),
    DRAGON_PICKAXE(12797, 12800, 11920),
    ODIUM_WARD(12807, 12802, 11926),
    MALEDICTION_WARD(12806, 12802, 11924),
    GRANITE_MAUL(12848, 12849, 4153);
    public static final UpgradeKit[] values = values();
    public static final Int2ObjectOpenHashMap<UpgradeKit> MAPPED_VALUES = new Int2ObjectOpenHashMap<>(values.length);

    static {
        for (final UpgradeKit val : values) {
            MAPPED_VALUES.put(val.completeItem, val);
        }
    }

    private final int completeItem;
    private final int kit;
    private final int baseItem;

    UpgradeKit(final int completeItem, final int kit, final int baseItem) {
        this.completeItem = completeItem;
        this.kit = kit;
        this.baseItem = baseItem;
    }

    public int getCompleteItem() {
        return this.completeItem;
    }

    public int getKit() {
        return this.kit;
    }

    public int getBaseItem() {
        return this.baseItem;
    }
}
