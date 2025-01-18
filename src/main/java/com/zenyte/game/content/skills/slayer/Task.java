package com.zenyte.game.content.skills.slayer;

import com.zenyte.game.world.region.Area;

/**
 * @author Kris | 5. nov 2017 : 21:22.52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class Task {
    private final SlayerMaster slayerMaster;
    private final int weight;
    private final int minimumAmount;
    private final int maximumAmount;
    private final Class<? extends Area>[] areas;

    public Task(final SlayerMaster master, final int weight, final int minimumAmount, final int maximumAmount) {
        this(master, weight, minimumAmount, maximumAmount, (Class<? extends Area>[]) null);
    }

    @SafeVarargs
    public Task(final SlayerMaster master, final int weight, final int minimumAmount, final int maximumAmount, final Class<? extends Area>... areas) {
        this.slayerMaster = master;
        this.weight = weight;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.areas = areas;
    }

    public SlayerMaster getSlayerMaster() {
        return this.slayerMaster;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getMinimumAmount() {
        return this.minimumAmount;
    }

    public int getMaximumAmount() {
        return this.maximumAmount;
    }

    public Class<? extends Area>[] getAreas() {
        return this.areas;
    }
}
