package com.zenyte.game.world.region;

import com.zenyte.game.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kris | 18. veebr 2018 : 3:11.44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class DynamicChunk {
    private final int regionId;
    private final int x;
    private final int y;
    private final int plane;
    private final int rotation;
    private int[][][] masks;
    private Map<Byte, WorldObject> objects;

    public DynamicChunk(final int regionId, final int x, final int y, final int plane, final int rotation) {
        this.regionId = regionId;
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.rotation = rotation;
        this.objects = new HashMap<>();
        this.masks = new int[4][8][8];
    }

    public DynamicChunk getRotatedChunk(final int rotation) {
        final DynamicChunk chunk = new DynamicChunk(regionId, x, y, plane, rotation);
        chunk.objects = new HashMap<>(this.objects);
        final int[][][] masks = new int[4][8][8];
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 8; x++) {
                System.arraycopy(this.masks[z][x], 0, masks[z][x], 0, 8);
            }
        }
        chunk.masks = masks;
        return chunk;
    }

    @Override
    public int hashCode() {
        return x | y << 11 | plane << 22;
    }

    public int getRegionId() {
        return this.regionId;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getPlane() {
        return this.plane;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int[][][] getMasks() {
        return this.masks;
    }

    public Map<Byte, WorldObject> getObjects() {
        return this.objects;
    }
}
