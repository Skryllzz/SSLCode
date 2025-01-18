package com.zenyte.game.world.object;

/**
 * @author Kris | 1. apr 2018 : 4:02.52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>}
 */
public final class AttachedObject {
    private final WorldObject object;
    private final int startTime;
    private final int endTime;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;

    public AttachedObject(final WorldObject object, final int startTime, final int endTime, final int minX, final int maxX, final int minY, final int maxY) {
        this.object = object;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public WorldObject getObject() {
        return this.object;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxY() {
        return this.maxY;
    }
}
