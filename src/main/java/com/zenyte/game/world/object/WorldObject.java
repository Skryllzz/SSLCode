package com.zenyte.game.world.object;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Region;
import mgi.types.config.ObjectDefinitions;

public class WorldObject extends Location {
    private int objectHash;

    public WorldObject(final int id, final int type, final int rotation, final int x, final int y, final int plane) {
        super(x, y, plane);
        objectHash = (id & 65535) | ((type & 31) << 16) | ((rotation & 3) << 21);
    }

    public WorldObject(final int id, final int type, final int rotation, final Location tile) {
        super(tile.getX(), tile.getY(), tile.getPlane());
        objectHash = (id & 65535) | ((type & 31) << 16) | ((rotation & 3) << 21);
    }

    public WorldObject(final WorldObject object) {
        super(object.getX(), object.getY(), object.getPlane());
        objectHash = (object.getId() & 65535) | ((object.getType() & 31) << 16) | ((object.getRotation() & 3) << 21) | (object.isLocked() ? (1 << 24) : 0);
    }

    public int getId() {
        return objectHash & 65535;
    }

    public void setId(final int id) {
        objectHash = (id & 65535) | ((getType() & 31) << 16) | ((getRotation() & 3) << 21);
    }

    public final int getType() {
        return (objectHash >> 16) & 31;
    }

    public void setType(final int type) {
        objectHash = (getId() & 65535) | ((type & 31) << 16) | ((getRotation() & 3) << 21);
    }

    public final int getRotation() {
        return (objectHash >> 21) & 3;
    }

    public void setRotation(final int rotation) {
        objectHash = (getId() & 65535) | ((getType() & 31) << 16) | ((rotation & 3) << 21);
    }

    public ObjectDefinitions getDefinitions() {
        return ObjectDefinitions.get(getId());
    }

    public String getName() {
        return getDefinitions().getName();
    }

    public int getId(final Player player) {
        int transformedId = getId();
        final ObjectDefinitions defs = getDefinitions();
        if (defs.getVarp() != -1 || defs.getVarbit() != -1) {
            final int[] transmogrificationIds = defs.getTransformedIds();
            final int varValue = defs.getVarp() != -1 ? player.getVarManager().getValue(defs.getVarp()) : player.getVarManager().getBitValue(defs.getVarbit());
            transformedId = transmogrificationIds[varValue];
        }
        return transformedId;
    }

    public Direction getFaceDirection() {
        return getRotation() == 1 ? Direction.NORTH : getRotation() == 0 ? Direction.WEST : getRotation() == 2 ? Direction.EAST : Direction.SOUTH;
    }

    public String getName(final Player player) {
        return ObjectDefinitions.get(getId(player)).getName();
    }

    public boolean isLocked() {
        return ((objectHash >> 24) & 1) == 1;
    }

    public void setLocked(final boolean value) {
        objectHash = objectHash & 16777215;
        if (value) {
            objectHash |= 1 << 24;
        }
    }

    public boolean exists() {
        final Region region = World.getRegion(this.getRegionId());
        return region.containsObject(getId(), getType(), this);
    }

    public boolean isMapObject() {
        return !World.getRegion(getRegionId()).containsSpawnedObject(this);
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof WorldObject obj)) {
            return false;
        }
        return obj.objectHash == objectHash && obj.getPositionHash() == getPositionHash();
    }

    @Override
    public String toString() {
        return getName() + ": " + getId() + ", " + getType() + ", " + getRotation() + ", " + isLocked() + "\nTile: " + getX() + ", " + getY() + ", " + getPlane() + ", region[" + getRegionId() + ", " + getRegionX() + ", " + getRegionY() + "], chunk[" + getChunkX() + ", " + getChunkY() + "], hash [" + getPositionHash() + "]";
    }
}
