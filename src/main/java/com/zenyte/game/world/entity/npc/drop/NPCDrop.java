package com.zenyte.game.world.entity.npc.drop;

/**
 * @author Kris | 19. dets 2017 : 14:27.42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class NPCDrop {
    private int itemId;
    private int minAmount;
    private int maxAmount;
    private float chance;

    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

    public int getMinAmount() {
        return this.minAmount;
    }

    public void setMinAmount(final int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public void setMaxAmount(final int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public float getChance() {
        return this.chance;
    }

    public void setChance(final float chance) {
        this.chance = chance;
    }
}
