package com.zenyte.game.world.entity;

public abstract class HitBar {

    public abstract int getType();

    public abstract int getPercentage();

    public int getDelay() {
        return 0;
    }

    public int interpolateTime() {
        return 0;
    }

    public int interpolatePercentage() {
        return 0;
    }

}