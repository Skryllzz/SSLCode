package com.zenyte.game.content.minigame.castlewars;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public enum CastlewarsRockPatch {

    SOUTH(new WorldObject(ObjectId.ROCKS_4437, 10, 1, new Location(2401, 9494, 0)), new Location(2400, 9496, 0), new Location(2403, 9496, 0), new Location(2400, 9493, 0), new Location(2403, 9493, 0)), EAST(new WorldObject(ObjectId.ROCKS_4437, 10, 1, new Location(2409, 9503, 0)), new Location(2408, 9502, 0), new Location(2408, 9505, 0), new Location(2411, 9502, 0), new Location(2411, 9505, 0)), NORTH(new WorldObject(ObjectId.ROCKS_4437, 10, 1, new Location(2400, 9512, 0)), new Location(2402, 9511, 0), new Location(2399, 9511, 0), new Location(2402, 9514, 0), new Location(2399, 9514, 0)), WEST(new WorldObject(ObjectId.ROCKS_4437, 10, 1, new Location(2391, 9501, 0)), new Location(2393, 9503, 0), new Location(2393, 9500, 0), new Location(2390, 9503, 0), new Location(2390, 9500, 0));

    public static final CastlewarsRockPatch[] VALUES = values();

    public static final Map<Integer, CastlewarsRockPatch> PATCHES = new HashMap<>();

    private final WorldObject patch;

    private final Location[] walls;

    CastlewarsRockPatch(final WorldObject patch, final Location... walls) {
        this.patch = patch;
        this.walls = walls;
    }

    public static CastlewarsRockPatch getData(final Location tile) {
        return PATCHES.get(tile.getPositionHash());
    }

    public WorldObject getPatch() {
        return this.patch;
    }

    public Location[] getWalls() {
        return this.walls;
    }
}
