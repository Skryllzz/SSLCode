package com.zenyte.game.content.grandexchange;

/**
 * @author Kris | 16/01/2019 01:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum InitialStock {
    AIR_RUNE(556, 1000000),
    WATER_RUNE(555, 1000000),
    EARTH_RUNE(557, 1000000),
    FIRE_RUNE(554, 1000000),
    MIND_RUNE(558, 250000),
    BODY_RUNE(559, 250000),
    CHAOS_RUNE(562, 100000),
    NATURE_RUNE(561, 20000),
    DEATH_RUNE(560, 100000),
    BLOOD_RUNE(565, 100000),
    SOUL_RUNE(566, 10000),
    LAW_RUNE(563, 20000),
    ASTRAL_RUNE(9075, 10000),
    BRONZE_ARROW(882, 100000),
    IRON_ARROW(884, 90000),
    STEEL_ARROW(886, 80000),
    MITHRIL_ARROW(888, 70000),
    ADAMANT_ARROW(890, 60000),
    RUNE_ARROW(892, 50000),
    BRONZE_BOLT(877, 50000),
    IRON_BOLT(9140, 45000),
    STEEL_BOLT(9141, 40000),
    MITHRIL_BOLT(9142, 30000),
    ADAMANT_BOLT(9143, 20000),
    RUNITE_BOLT(9144, 10000),
    BONE_BOLT(8882, 25000),
    HAMMER(2347, 1000),
    KNIFE(946, 1000),
    CHISEL(1755, 1000),
    TINDERBOX(590, 1000),
    SMALL_FISHING_NET(303, 1000),
    LARGE_FISHING_NET(305, 1000),
    FISHING_ROD(307, 1000),
    FLY_FISHING_ROD(309, 1000),
    LOBSTER_POT(301, 1000),
    HARPOON(311, 1000),
    SPADE(952, 1000);
    private final int id;
    private final int amount;

    InitialStock(final int id, final int amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() {
        return this.id;
    }

    public int getAmount() {
        return this.amount;
    }
}
