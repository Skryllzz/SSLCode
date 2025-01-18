package com.zenyte.game.parser.impl;

public class TempDefinition {
    private int id;
    private int attackAnimation = -1;
    private int blockAnimation = -1;
    private int deathAnimation = -1;

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getAttackAnimation() {
        return this.attackAnimation;
    }

    public void setAttackAnimation(final int attackAnimation) {
        this.attackAnimation = attackAnimation;
    }

    public int getBlockAnimation() {
        return this.blockAnimation;
    }

    public void setBlockAnimation(final int blockAnimation) {
        this.blockAnimation = blockAnimation;
    }

    public int getDeathAnimation() {
        return this.deathAnimation;
    }

    public void setDeathAnimation(final int deathAnimation) {
        this.deathAnimation = deathAnimation;
    }
}
