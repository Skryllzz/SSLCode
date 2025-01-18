package com.zenyte.game.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kris | 23. march 2018 : 21:43.00
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum Direction {
    SOUTH(0, 1, 6, 0, -1),
    SOUTH_WEST(256, 0, 5, -1, -1),
    WEST(512, 3, 3, -1, 0),
    NORTH_WEST(768, 5, 0, -1, 1),
    NORTH(1024, 6, 1, 0, 1),
    NORTH_EAST(1280, 7, 2, 1, 1),
    EAST(1536, 4, 4, 1, 0),
    SOUTH_EAST(1792, 2, 7, 1, -1);
    public static final Direction[] values = values();
    public static final Direction[] mainDirections = new Direction[]{SOUTH, WEST, NORTH, EAST};
    public static final Direction[] interCardinalDirections = new Direction[]{SOUTH_WEST, NORTH_WEST, NORTH_EAST, SOUTH_EAST};
    public static final Map<Integer, Direction> npcMap = new HashMap<>(values.length);
    private static final Map<Integer, Direction> map = new HashMap<>(values.length);
    public static final ObjectLists.UnmodifiableList<Direction> orderedByDirectionValue;

    static {
        for (final Direction dir : values) {
            map.put(dir.movementDirection, dir);
            npcMap.put(dir.NPCDirection, dir);
        }

        var orderedDirections = new ObjectArrayList<Direction>(values.length);
        map.values().stream().sorted(Comparator.comparingInt(Direction::getDirection)).forEach(orderedDirections::add);
        orderedByDirectionValue = (ObjectLists.UnmodifiableList<Direction>) ObjectLists.unmodifiable(orderedDirections);
    }

    private final int direction;
    private final int movementDirection;
    private final int NPCDirection;
    private final int offsetX;
    private final int offsetY;

    Direction(final int direction, final int movementDirection, final int NPCDirection, final int offsetX, final int offsetY) {
        this.direction = direction;
        this.movementDirection = movementDirection;
        this.NPCDirection = NPCDirection;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public static Direction getMovementDirection(final int value) {
        return map.get(value);
    }

    public static Direction getNPCDirection(final int value) {
        return npcMap.get(value);
    }

    @NotNull
    public Direction getCounterClockwiseDirection(int cycles) {
        final int targetDirection = (direction - (cycles << 8)) & 2047;
        for (Direction direction : values) {
            if (direction.getDirection() == targetDirection) {
                return direction;
            }
        }
        throw new RuntimeException();
    }

    public int getDirection() {
        return this.direction;
    }

    public int getMovementDirection() {
        return this.movementDirection;
    }

    public int getNPCDirection() {
        return this.NPCDirection;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }
}
