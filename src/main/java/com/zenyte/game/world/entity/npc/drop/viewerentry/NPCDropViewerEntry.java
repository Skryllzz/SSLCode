package com.zenyte.game.world.entity.npc.drop.viewerentry;

/**
 * @author Tommeh | 05/10/2019 | 19:03
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class NPCDropViewerEntry implements DropViewerEntry {
    private final int itemId;
    private final int npc;
    private final int minAmount;
    private final int maxAmount;
    private final double rate;
    private final String info;

    public NPCDropViewerEntry(final int itemId, final int npc, final int minAmount, final int maxAmount, final double rate, final String info) {
        this.itemId = itemId;
        this.npc = npc;
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

    public int getItemId() {
        return this.itemId;
    }

    public int getNpc() {
        return this.npc;
    }
}
