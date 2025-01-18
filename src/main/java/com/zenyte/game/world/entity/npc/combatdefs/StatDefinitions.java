package com.zenyte.game.world.entity.npc.combatdefs;

import java.util.Arrays;

/**
 * @author Kris | 18/11/2018 02:51
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class StatDefinitions {
    int[] combatStats = new int[5];
    int[] aggressiveStats = new int[5];
    int[] defensiveStats = new int[5];
    int[] otherBonuses = new int[3];

    int[] getArray(final StatType type) {
        final int index = type.ordinal() / 5;
        return index == 0 ? combatStats : index == 1 ? aggressiveStats : index == 2 ? defensiveStats : otherBonuses;
    }

    public void set(final StatType type, final int value) {
        getArray(type)[type.index()] = value;
    }

    public int get(final StatType type) {
        return getArray(type)[type.index()];
    }

    public StatDefinitions clone() {
        final StatDefinitions defs = new StatDefinitions();
        defs.combatStats = Arrays.copyOf(combatStats, combatStats.length);
        defs.aggressiveStats = Arrays.copyOf(aggressiveStats, aggressiveStats.length);
        defs.defensiveStats = Arrays.copyOf(defensiveStats, defensiveStats.length);
        defs.otherBonuses = Arrays.copyOf(otherBonuses, otherBonuses.length);
        return defs;
    }

    public int getAggressiveStat(final StatType type) {
        return aggressiveStats[type.ordinal()];
    }

    public int[] getCombatStats() {
        return this.combatStats;
    }

    public void setCombatStats(final int[] combatStats) {
        this.combatStats = combatStats;
    }

    public int[] getAggressiveStats() {
        return this.aggressiveStats;
    }

    public void setAggressiveStats(final int[] aggressiveStats) {
        this.aggressiveStats = aggressiveStats;
    }

    public int[] getDefensiveStats() {
        return this.defensiveStats;
    }

    public void setDefensiveStats(final int[] defensiveStats) {
        this.defensiveStats = defensiveStats;
    }

    public int[] getOtherBonuses() {
        return this.otherBonuses;
    }

    public void setOtherBonuses(final int[] otherBonuses) {
        this.otherBonuses = otherBonuses;
    }
}
