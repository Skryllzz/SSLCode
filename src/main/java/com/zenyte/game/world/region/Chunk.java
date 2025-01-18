package com.zenyte.game.world.region;

import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.utils.efficientarea.EfficientArea;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Kris | 28. march 2018 : 17:28.55
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class Chunk implements MapSquare {
    /**
     * The bitpacked if of this chunk.
     */
    private final int chunkId;
    /**
     * List of players in the chunk.
     */
    private final List<Player> players;
    /**
     * List of NPCs in the chunk.
     */
    private final List<NPC> npcs;
    /**
     * List of objects that have been spawned.
     */
    private final Short2ObjectMap<WorldObject> spawnedObjects;
    /**
     * List of original map-bound objects that have been removed or replaced.
     */
    private final Short2ObjectMap<WorldObject> originalObjects;
    private final Set<FloorItem> floorItems;
    private long referenceTime;
    @Nullable
    private List<RSArea> multiPolygons;

    public Chunk(final int hash) {
        chunkId = hash;
        players = new ArrayList<>();
        npcs = new ArrayList<>();
        spawnedObjects = new Short2ObjectOpenHashMap<>();
        originalObjects = new Short2ObjectOpenHashMap<>();
        floorItems = new HashSet<>();
    }

    public static int getChunkHash(final int chunkX, final int chunkY, final int height) {
        return chunkX | chunkY << 11 | height << 22;
    }

    public Chunk resetReferenceTime() {
        referenceTime = System.currentTimeMillis();
        return this;
    }

    public boolean isFree() {
        return players.isEmpty() && npcs.isEmpty() && (spawnedObjects == null || spawnedObjects.isEmpty()) && (originalObjects == null || originalObjects.isEmpty()) && (floorItems == null || floorItems.isEmpty()) && (multiPolygons == null || multiPolygons.isEmpty());
    }

    public void addFloorItem(final FloorItem item) {
        floorItems.add(item);
        World.getAllFloorItems().add(item);
        if (item.getInvisibleTicks() <= 0) {
            World.destroyFloorItem(getRemovedItemIfCapReached());
        }
    }

    public void removeFloorItem(final FloorItem item, final boolean removeFromGlobal) {
        floorItems.remove(item);
        if (removeFromGlobal) World.getAllFloorItems().remove(item);
    }

    public FloorItem getRemovedItemIfCapReached() {
        if (floorItems.size() <= 128) {
            return null;
        }
        int count = 0;
        for (final FloorItem item : floorItems) {
            if (item == null || item.getInvisibleTicks() > 0 || !item.isTradable()) {
                continue;
            }
            count++;
        }
        if (count > 129) {
            FloorItem cheapest = null;
            for (final FloorItem item : floorItems) {
                if (item == null || item.getInvisibleTicks() > 0 || !item.isTradable()) {
                    continue;
                }
                if (cheapest == null || ((long) item.getSellPrice() * item.getAmount()) < ((long) cheapest.getSellPrice() * cheapest.getAmount())) {
                    cheapest = item;
                }
            }
            return cheapest;
        }
        return null;
    }

    public void clearFloorItems() {
        if (floorItems.isEmpty()) return;
        for (final FloorItem floorItem : floorItems) {
            World.getAllFloorItems().remove(floorItem);
        }
        floorItems.clear();
    }

    public void wipe() {
        clearFloorItems();
        spawnedObjects.clear();
        originalObjects.clear();
    }

    public void addMultiPolygon(final RSArea area) {
        if (multiPolygons == null) multiPolygons = new ArrayList<>();
        multiPolygons.add(area);
    }

    public int getChunkX() {
        return chunkId & 2047;
    }

    public int getChunkY() {
        return chunkId >> 11 & 2047;
    }

    public int getChunkZ() {
        return chunkId >> 22;
    }

    @Override
    public List<NPC> getNPCs() {
        return npcs;
    }

    @Override
    public boolean equals(@org.jetbrains.annotations.Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof Chunk other)) return false;
        if (this.getChunkId() != other.getChunkId()) return false;
        if (this.getReferenceTime() != other.getReferenceTime()) return false;
        final Object this$players = this.getPlayers();
        final Object other$players = other.getPlayers();
        if (!Objects.equals(this$players, other$players)) return false;
        final Object this$npcs = this.getNPCs();
        final Object other$npcs = other.getNPCs();
        if (!Objects.equals(this$npcs, other$npcs)) return false;
        final Object this$spawnedObjects = this.getSpawnedObjects();
        final Object other$spawnedObjects = other.getSpawnedObjects();
        if (!Objects.equals(this$spawnedObjects, other$spawnedObjects)) return false;
        final Object this$originalObjects = this.getOriginalObjects();
        final Object other$originalObjects = other.getOriginalObjects();
        if (!Objects.equals(this$originalObjects, other$originalObjects)) return false;
        final Object this$floorItems = this.getFloorItems();
        final Object other$floorItems = other.getFloorItems();
        if (!Objects.equals(this$floorItems, other$floorItems)) return false;
        final Object this$multiPolygons = this.getMultiPolygons();
        final Object other$multiPolygons = other.getMultiPolygons();
        return Objects.equals(this$multiPolygons, other$multiPolygons);
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getChunkId();
        final long $referenceTime = this.getReferenceTime();
        result = result * PRIME + (int) ($referenceTime >>> 32 ^ $referenceTime);
        final Object $players = this.getPlayers();
        result = result * PRIME + ($players == null ? 43 : $players.hashCode());
        final Object $npcs = this.getNPCs();
        result = result * PRIME + ($npcs == null ? 43 : $npcs.hashCode());
        final Object $spawnedObjects = this.getSpawnedObjects();
        result = result * PRIME + ($spawnedObjects == null ? 43 : $spawnedObjects.hashCode());
        final Object $originalObjects = this.getOriginalObjects();
        result = result * PRIME + ($originalObjects == null ? 43 : $originalObjects.hashCode());
        final Object $floorItems = this.getFloorItems();
        result = result * PRIME + ($floorItems == null ? 43 : $floorItems.hashCode());
        final Object $multiPolygons = this.getMultiPolygons();
        result = result * PRIME + ($multiPolygons == null ? 43 : $multiPolygons.hashCode());
        return result;
    }

    /**
     * The bitpacked if of this chunk.
     */
    public int getChunkId() {
        return this.chunkId;
    }

    public long getReferenceTime() {
        return this.referenceTime;
    }

    /**
     * List of players in the chunk.
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * List of objects that have been spawned.
     */
    public Short2ObjectMap<WorldObject> getSpawnedObjects() {
        return this.spawnedObjects;
    }

    /**
     * List of original map-bound objects that have been removed or replaced.
     */
    public Short2ObjectMap<WorldObject> getOriginalObjects() {
        return this.originalObjects;
    }

    public Set<FloorItem> getFloorItems() {
        return this.floorItems;
    }

    @Nullable
    public List<RSArea> getMultiPolygons() {
        return this.multiPolygons;
    }

    public static final class RSArea {
        private final int height;
        private final EfficientArea area;

        public RSArea(final int height, final EfficientArea area) {
            this.height = height;
            this.area = area;
        }

        public int getHeight() {
            return this.height;
        }

        public EfficientArea getArea() {
            return this.area;
        }
    }
}
