package com.zenyte.game.world.entity.masks;

import com.zenyte.game.world.entity.Location;

/**
 * @author Tom
 */
public class ForceMovement {
    public static final int SOUTH = 0;
    public static final int WEST = 512;
    public static final int NORTH = 1024;
    public static final int EAST = 1536;
    private Location toFirstTile;
    private Location toSecondTile;
    private int firstTileTicketDelay;
    private int secondTileTicketDelay;
    private int direction;

    public ForceMovement(final Location toFirstTile, final int firstTileTicketDelay, final int direction) {
        this(toFirstTile, firstTileTicketDelay, null, 0, direction);
    }

    public ForceMovement(final Location toFirstTile, final int firstTileTicketDelay, final Location toSecondTile, final int secondTileTicketDelay, final int direction) {
        this.toFirstTile = toFirstTile;
        this.firstTileTicketDelay = firstTileTicketDelay;
        this.toSecondTile = toSecondTile;
        this.secondTileTicketDelay = secondTileTicketDelay;
        this.direction = direction;
    }

    public Location getToFirstTile() {
        return this.toFirstTile;
    }

    public void setToFirstTile(final Location toFirstTile) {
        this.toFirstTile = toFirstTile;
    }

    public Location getToSecondTile() {
        return this.toSecondTile;
    }

    public void setToSecondTile(final Location toSecondTile) {
        this.toSecondTile = toSecondTile;
    }

    public int getFirstTileTicketDelay() {
        return this.firstTileTicketDelay;
    }

    public void setFirstTileTicketDelay(final int firstTileTicketDelay) {
        this.firstTileTicketDelay = firstTileTicketDelay;
    }

    public int getSecondTileTicketDelay() {
        return this.secondTileTicketDelay;
    }

    public void setSecondTileTicketDelay(final int secondTileTicketDelay) {
        this.secondTileTicketDelay = secondTileTicketDelay;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(final int direction) {
        this.direction = direction;
    }
}
