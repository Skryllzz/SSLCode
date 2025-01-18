package com.zenyte.game.world.entity.npc.drop.viewerentry;

/**
 * @author Tommeh | 05/10/2019 | 19:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ItemDropViewerEntry implements DropViewerEntry {
    private final int item;
    private final int minAmount;
    private final int maxAmount;
    private final double rate;
    private final String info;

    public ItemDropViewerEntry(final int item, final int minAmount, final int maxAmount, final double rate, final String info) {
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.rate = rate;
        this.info = info;
    }

    @Override
    public int getMinAmount() {
        return minAmount;
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public boolean isPredicated() {
        return !info.isEmpty();
    }

    public int getItem() {
        return this.item;
    }
}
