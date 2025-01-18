package com.zenyte.game.world.entity.npc.spawns;

import com.zenyte.game.util.Direction;
import org.jetbrains.annotations.NotNull;


public final class NPCSpawn {
    private int id;
    private int x;
    private int y;
    private int z;
    private Direction direction = Direction.SOUTH;
    private Integer radius = 0;

    public NPCSpawn(final int id, final int x, final int y, final int z, final Direction direction, final Integer radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.radius = radius;
    }

    public NPCSpawn() {
    }

    @NotNull
    @Override
    public String toString() {
        return "NPCSpawn(id=" + this.getId() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", direction=" + this.getDirection() + ", radius=" + this.getRadius() + ")";
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(final Direction direction) {
        this.direction = direction;
    }

    public Integer getRadius() {
        return this.radius;
    }

    public void setRadius(final Integer radius) {
        this.radius = radius;
    }
    //@Getter @Setter private int knownIndex;
}
