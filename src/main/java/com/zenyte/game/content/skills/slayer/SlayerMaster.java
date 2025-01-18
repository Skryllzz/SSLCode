package com.zenyte.game.content.skills.slayer;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Kris | 5. nov 2017 : 21:22.44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public enum SlayerMaster {
    TURAEL(401, 1, 1, 0, "in Burthorpe"),
    KRYSTILIA(7663, 1, 1, 25, "in Edgeville"),
    MAZCHNA(402, 1, 20, 2, "in Canifis"),
    VANNAKA(403, 1, 40, 4, "within the Edgeville dungeon"),
    CHAELDAR(404, 1, 70, 10, "in Zanaris"),
    NIEVE(490, 1, 85, 12, "in Tree Gnome Stronghold"),
    DURADEL(405, 50, 100, 15, "in Shilo Village"),
    KONAR_QUO_MATEN(8623, 1, 75, 18, "On Mount Karuulm");
    public static final SlayerMaster[] VALUES = values();
    public static final Int2ObjectOpenHashMap<SlayerMaster> MAPPED_MASTERS = new Int2ObjectOpenHashMap<>(VALUES.length);

    static {
        for (final SlayerMaster master : VALUES) {
            MAPPED_MASTERS.put(master.npcId, master);
        }
    }

    private final int npcId;
    private final int slayerRequirement;
    private final int combatRequirement;
    private final int pointsPerTask;
    private final String location;

    SlayerMaster(final int npcId, final int slayerRequirement, final int combatRequirement, final int pointsPerTask, final String location) {
        this.npcId = npcId;
        this.slayerRequirement = slayerRequirement;
        this.combatRequirement = combatRequirement;
        this.pointsPerTask = pointsPerTask;
        this.location = location;
    }

    public static boolean isMaster(final int id) {
        return MAPPED_MASTERS.containsKey(id);
    }

    public final int getMultiplier(final int taskNum) {
        if (taskNum % 1000 == 0) {
            return 50;
        } else if (taskNum % 250 == 0) {
            return 35;
        } else if (taskNum % 100 == 0) {
            return 25;
        } else if (taskNum % 50 == 0) {
            return 15;
        } else if (taskNum % 10 == 0) {
            return 5;
        }
        return 1;
    }

    @Override
    public String toString() {
        return StringUtils.capitalize(name().toLowerCase());
    }

    public int getNpcId() {
        return this.npcId;
    }

    public int getSlayerRequirement() {
        return this.slayerRequirement;
    }

    public int getCombatRequirement() {
        return this.combatRequirement;
    }

    public int getPointsPerTask() {
        return this.pointsPerTask;
    }

    public String getLocation() {
        return this.location;
    }
}
