package com.zenyte.game.world;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.achievementdiary.diaries.WesternProvincesDiary;
import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.grandexchange.GrandExchangeHandler;
import com.zenyte.game.content.minigame.duelarena.Duel;
import com.zenyte.game.content.minigame.duelarena.area.ArenaArea;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.containers.LootingBag;
import com.zenyte.game.packet.out.*;
import com.zenyte.game.parser.scheduled.ScheduledExternalizableManager;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.npc.AbstractNPCManager;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.spawns.NPCSpawn;
import com.zenyte.game.world.entity.pathfinding.Flags;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerInformation;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerResult;
import com.zenyte.game.world.entity.player.container.impl.RunePouch;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.login.LoginManager;
import com.zenyte.game.world.entity.player.login.LoginRequest;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.game.world.flooritem.GlobalItem;
import com.zenyte.game.world.object.AttachedObject;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.*;
import com.zenyte.game.world.region.area.plugins.DropPlugin;
import com.zenyte.network.NetworkConstants;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.events.FloorItemSpawnEvent;
import com.zenyte.plugins.events.ServerShutdownEvent;
import com.zenyte.utils.MultiwayArea;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Kris | 24. sept 2017 : 4:13.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>} Contains a static game world. Supports only one world as
 * of right now, not going to create singletons as multi world support doesn't exist anyways.
 */
public final class World {
    public static final int PLAYER_LIST_CAPACITY = 2048;
    public static final int NPC_LIST_CAPACITY = Short.MAX_VALUE;
    public static final Map<String, Player> NAMED_PLAYERS = new HashMap<>(NetworkConstants.PLAYER_CAP);
    public static final Int2ObjectAVLTreeMap<Player> USED_PIDS = new Int2ObjectAVLTreeMap<>();
    public static final IntArrayList AVAILABLE_PIDS = new IntArrayList(IntStream.rangeClosed(0, 2000).boxed().collect(Collectors.toList()));
    /**
     * A list holding the duel history.
     */
    public static final LinkedList<String> LATEST_DUELS = new LinkedList<>();
    /**
     * A map containing all Regions as Integers.
     */
    public static final Int2ObjectOpenHashMap<Region> regions = new Int2ObjectOpenHashMap<>(1000);
    public static final Class<?>[] NPC_INVOCATION_ARGUMENTS = new Class[]{int.class, Location.class, Direction.class, int.class};
    public static final Set<NPC> pendingRemovedNPCs = new ObjectOpenHashSet<>();
    private static final Logger log = LoggerFactory.getLogger(World.class);
    /**
     * A list holding all Player-Entities online.
     */
    private static final EntityList<Player> PLAYERS = new EntityList<>(PLAYER_LIST_CAPACITY);
    /**
     * A list holding all NPC-Entities online.
     */
    private static final EntityList<NPC> NPCS = new EntityList<>(NPC_LIST_CAPACITY);
    /**
     * A map containing all the chunks loaded.
     */
    private static final Int2ObjectOpenHashMap<Chunk> chunks = new Int2ObjectOpenHashMap<>(5000);
    private static final IntFunction<Chunk> cFunction = Chunk::new;
    private static final Object chunkLock = new Object();
    private static final Set<FloorItem> allFloorItems = new HashSet<>(5000);
    private static final SoundEffect itemTakeSound = new SoundEffect(2582);
    /**
     * The Gson loader
     */
    private static boolean updating;
    private static int updateTimer;
    private static int HELPFUL_TIP_INDEX = 0;

    static {
        for (int i = 0; i < 50; i++) {
            LATEST_DUELS.add("");
        }
    }

    public static Optional<Player> getPlayer(@NotNull final String name) {
        final String username = Utils.formatUsername(name);
        return Optional.ofNullable(NAMED_PLAYERS.get(username));
    }

    public static void setShutdown(final int ticks) {
        assert ticks >= 0 : "Shutdown timer must be positive";
        if (!updating) {
            WorldTasksManager.schedule(() -> {
                if (--updateTimer <= 0) {
                    CoresManager.setShutdown(true);
                }
            }, 0, 0);
        }
        updateTimer = ticks;
        updating = true;
        for (final Player player : getPlayers()) {
            player.send(new UpdateRebootTimer(ticks));
        }
    }

    public static void shutdown() {
        log.info("Starting shutdown sequence.");
        CoresManager.worldThread.setRunning(false);
        try {
            CoresManager.worldThread.join(Duration.ofMillis(5000));
        } catch (InterruptedException e) {
            log.error("Failed to join world thread for shutdown sequence.", e);
        }
        log.info("Ending duels and returning items to users.");
        Duel.beforeShutdown();
        log.info("Saving all players' accounts and logging them off.");
        synchronized (LoginManager.writeLock) {
            for (final Player player : World.getPlayers()) {
                CoresManager.getLoginManager().savePlayer(player);
            }
        }
        getPlayers().forEach(World::saveAndKick);
        log.info("Shutting down login manager - no longer processing new requests.");
        CoresManager.getLoginManager().shutdown();
        log.info("Doing a final process cycle on login management to ensure accounts are saved.");
        CoresManager.getLoginManager().process();
        log.info("Requesting shutdown on slow executor and grand exchange executor.");
        CoresManager.closeServices();
        log.info("Processing server shutdown event.");
        PluginManager.post(new ServerShutdownEvent());
        log.info("Saving all of grand exchange offers.");
        GrandExchangeHandler.save();
        log.info("Saving all the scheduled externalizable tasks.");
        ScheduledExternalizableManager.save();
        log.info("Waiting for any remaining characters to be saved.");
        CoresManager.getLoginManager().waitForShutdown();
        log.info("Joining all the threads and threadpools.");
        CoresManager.join();
        log.info("Server shutdown complete.");
        System.exit(0);
    }

    private static void saveAndKick(final Player player) {
        try {
            final Logout packet = new Logout();
            player.getSession().getChannel().writeAndFlush(packet.encode());
            packet.log(player);
            player.finish();
            CoresManager.getLoginManager().save(player);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static void addLoginRequest(final PlayerInformation info) {
        final long time = System.currentTimeMillis();
        final LoginRequest loginRequest = new LoginRequest(info, time);
        if (!loginRequest.checkPreconditions()) {
            return;
        }
        CoresManager.getLoginManager().load(time, info, player -> loginRequest.log(player, System.currentTimeMillis() - time >= TimeUnit.SECONDS.toMillis(30)));
    }

    public static Int2ObjectOpenHashMap<Region> getRegions() {
        return regions;
    }

    public static Region getRegion(final int id) {
        return getRegion(id, false);
    }

    public static void loadRegion(final int id) {
        Region region = regions.get(id);
        if (region == null) {
            region = new Region(id);
            regions.put(id, region);
        }
        region.load();
    }

    @NotNull
    public static Chunk getChunk(final int hash) {
        synchronized (chunkLock) {
            return chunks.computeIfAbsent(hash, cFunction).resetReferenceTime();
        }
    }

    public static void deallocateChunk(final int hash) {
        synchronized (chunkLock) {
            chunks.remove(hash);
        }
    }

    public static void purgeChunks() {
        final long nanoTime = System.nanoTime();
        final long ms = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5);
        final MutableInt count = new MutableInt();
        synchronized (chunkLock) {
            chunks.int2ObjectEntrySet().removeIf(chunkEntry -> {
                final int key = chunkEntry.getIntKey();
                //If chunk is in instances map area
                if ((key & 2047) >= 800) {
                    final Chunk chunk = chunkEntry.getValue();
                    if (chunk.getReferenceTime() < ms) {
                        if (chunk.isFree()) {
                            count.increment();
                            return true;
                        }
                    }
                }
                return false;
            });
        }
        log.info("Purged " + count.intValue() + " out-of-date chunks in " + (System.nanoTime() - nanoTime) + " nanoseconds with " + chunks.size() + " chunks remaining.");
    }

    @NotNull
    public static Chunk getChunk(final int x, final int y, final int z) {
        return getChunk((x >> 3) | ((y >> 3) << 11) | (z << 22));
    }

    public static void init() {
        initGrandExchangeSavingTask();
        initHelpfulTipTask();
    }

    public static void initTasks() {
        scheduleFloorItemTask();
    }

    private static void scheduleFloorItemTask() {
        final LinkedList<FloorItem> list = new LinkedList<>();
        WorldTasksManager.schedule(() -> {
            try {
                final Iterator<FloorItem> iterator = allFloorItems.iterator();
                FloorItem fItem;
                while (iterator.hasNext()) {
                    fItem = iterator.next();
                    final int invisibleTicks = fItem.getInvisibleTicks();
                    if (invisibleTicks > 0) {
                        fItem.setInvisibleTicks(invisibleTicks - 1);
                        if (invisibleTicks <= 1) {
                            if (fItem.isTradable() && fItem.getVisibleTicks() > 0) {
                                turnFloorItemPublic(fItem);
                                final Location tile = fItem.getLocation();
                                final FloorItem removedItem = World.getChunk(tile.getX(), tile.getY(), tile.getPlane()).getRemovedItemIfCapReached();
                                if (removedItem != null) {
                                    list.add(removedItem);
                                }
                            } else {
                                if (destroyFloorItem(fItem, false)) {
                                    iterator.remove();
                                }
                            }
                        }
                    } else {
                        final int visibleTicks = fItem.getVisibleTicks();
                        if (visibleTicks > 0) {
                            fItem.setVisibleTicks(visibleTicks - 1);
                            if (visibleTicks <= 1) {
                                if (destroyFloorItem(fItem, false)) {
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }
                for (final FloorItem removedItem : list) {
                    destroyFloorItem(removedItem);
                }
                list.clear();
            } catch (final Exception e) {
                log.error("", e);
            }
        }, 0, 0);
    }

    public static synchronized Region getRegion(final int id, final boolean load) {
        Region region = regions.get(id);
        if (region == null) {
            region = new Region(id);
            regions.put(id, region);
        }
        if (load) {
            region.load();
        }
        return region;
    }

    public static void addNPC(final NPC npc) {
        if (pendingRemovedNPCs.remove(npc)) {
            return;
        }
        NPCS.add(npc);
    }

    public static void removeNPC(final NPC npc) {
        pendingRemovedNPCs.add(npc);
    }

    public static NPC invoke(final int id, final Location location, final Direction direction, final int radius) {
        final NPCDefinitions definitions = NPCDefinitions.get(id);
        if (definitions == null) {
            throw new RuntimeException("Unable to invoke npc " + id);
        }
        final String name = definitions.getName().toLowerCase();
        try {
            final Class<? extends NPC> c = AbstractNPCManager.get(id, name);
            return c.getDeclaredConstructor(NPC_INVOCATION_ARGUMENTS).newInstance(id, location, direction, radius);
        } catch (final Exception e) {
            e.printStackTrace();
            return new NPC(id, location, direction, radius);
        }
    }

    public static NPC spawnNPC(final NPCSpawn spawn, final int id, final Location location, final Direction direction, final int radius) {
        final NPCDefinitions definitions = NPCDefinitions.get(id);
        if (definitions == null) {
            return null;
        }
        final String name = definitions.getName().toLowerCase();
        try {
            final Class<? extends NPC> c = AbstractNPCManager.get(id, name);
            final Constructor<? extends NPC> clazz = c.getDeclaredConstructor(NPC_INVOCATION_ARGUMENTS);
            clazz.setAccessible(true);
            final NPC npc = clazz.newInstance(id, location, direction, radius);
            npc.spawn();
            npc.setNpcSpawn(spawn);
            return npc;
        } catch (final Exception e) {
            e.printStackTrace();
            final NPC npc = new NPC(id, location, direction, radius);
            npc.spawn();
            npc.setNpcSpawn(spawn);
            return npc;
        }
    }

    public static NPC spawnNPC(final int id, final Location location, final Direction direction, final int radius) {
        final NPCDefinitions definitions = NPCDefinitions.get(id);
        if (definitions == null) {
            return null;
        }
        final String name = definitions.getName().toLowerCase();
        try {
            final Class<? extends NPC> c = AbstractNPCManager.get(id, name);
            final Constructor<? extends NPC> clazz = c.getDeclaredConstructor(NPC_INVOCATION_ARGUMENTS);
            clazz.setAccessible(true);
            final NPC npc = clazz.newInstance(id, location, direction, radius);
            npc.spawn();
            return npc;
        } catch (final Exception e) {
            e.printStackTrace();
            final NPC npc = new NPC(id, location, direction, radius);
            npc.spawn();
            return npc;
        }
    }

    public static NPC spawnNPC(final NPC npc) {
        final NPCDefinitions definitions = NPCDefinitions.get(npc.getId());
        if (definitions == null) {
            return null;
        }
        npc.spawn();
        return npc;
    }

    public static void sendMessage(final MessageType type, final String message) {
        World.getPlayers().stream().filter(Objects::nonNull).forEach(p -> p.getPacketDispatcher().sendGameMessage(message, type));
    }

    public static boolean containsSpawnedObject(final WorldObject object) {
        return getRegion(object.getRegionId()).containsSpawnedObject(object);
    }

    public static NPC spawnNPC(final int id, final Location location) {
        return spawnNPC(id, location, Direction.SOUTH, 3);
    }

    private static void initGrandExchangeSavingTask() {
        CoresManager.getServiceProvider().scheduleRepeatingTask(() -> {
            try {
                GrandExchangeHandler.save();
            } catch (final Exception e) {
                System.err.println("FATAL: Failed to save grand exchange offers.");
                e.printStackTrace();
            }
        }, 30, 30);
    }

    private static void initHelpfulTipTask() {
        WorldTasksManager.schedule(() -> {
            final String message = WorldBroadcasts.HELPFUL_TIPS[HELPFUL_TIP_INDEX++];
            WorldBroadcasts.broadcast(null, BroadcastType.HELPFUL_TIP, message);
            if (HELPFUL_TIP_INDEX == WorldBroadcasts.HELPFUL_TIPS.length) {
                HELPFUL_TIP_INDEX = 0;
            }
        }, 0, 1000);
    }

    public static void sendAttachedObject(final Player target, final AttachedObject object) {
        SceneSynchronization.forEach(object.getObject(), player -> new LocCombine(target.getIndex(), object));
    }

    public static ProjectileResult scheduleProjectile(final Position shooter, final Position receiver, final Projectile projectile) {
        final ProjectileResult result = new ProjectileResult();
        result.execute(sendProjectile(shooter, receiver, projectile));
        return result;
    }

    public static int sendProjectile(final Position shooter, final Position receiver, final Projectile projectile) {
        return sendProjectile(shooter, receiver, projectile, Integer.MIN_VALUE);
    }

    public static void forEachObject(@NotNull final Location southWesternCorner, final int radius, @NotNull final Consumer<WorldObject> consumer) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius may not be negative.");
        }
        final int minX = (southWesternCorner.getX() >> 6) << 6;
        final int minY = (southWesternCorner.getY() >> 6) << 6;
        final int maxX = ((southWesternCorner.getX() + radius) >> 6) << 6;
        final int maxY = ((southWesternCorner.getY() + radius) >> 6) << 6;
        int regionId = -1;
        Region region = null;
        for (int x = minX; x <= maxX; x += 64) {
            for (int y = minY; y <= maxY; y += 64) {
                final int rId = Location.getRegionId(x, y);
                if (regionId != rId) {
                    region = World.getRegion(regionId = rId, true);
                }
                assert region != null;
                final Short2ObjectMap<WorldObject> objects = region.getObjects();
                if (objects == null) {
                    continue;
                }
                for (final WorldObject object : objects.values()) {
                    if (object == null || !object.withinDistance(southWesternCorner, radius)) {
                        continue;
                    }
                    consumer.accept(object);
                }
            }
        }
    }

    public static int sendProjectile(final Position shooter, final Position receiver, final Projectile projectile, final int speed) {
        final boolean isEntity = shooter instanceof Entity;
        final Location from = isEntity ? ((Entity) shooter).getMiddleLocation() : shooter.getPosition();
        final int size = !isEntity ? 1 : ((Entity) shooter).getSize();
        final Location to = receiver.getPosition();
        final boolean adjust = isEntity && (size & 1) == 0;
        Location f = adjust ? new Location(from) : from;
        int sizeOffsetAdjust = 0;
        if (adjust) {
            double dir = (((Math.atan2(-(to.getX() - f.getX()), -(to.getY() - f.getY())) * 325.949)));
            if (dir < 0) {
                dir += 2048;
            }
            if (dir >= 512 && dir < 1024) {
                f.moveLocation(0, 1, 0);
            } else if (dir >= 1024 && dir < 1536) {
                f.moveLocation(1, 1, 0);
            } else if (dir >= 1536) {
                f.moveLocation(1, 0, 0);
            } else {
                sizeOffsetAdjust = 1;
            }
        }
        final int offset = (int) Math.min(255, isEntity ? ((Math.ceil((size - sizeOffsetAdjust) / 2.0F) * 64)) : projectile.getDistanceOffset());
        //Since projectiles aren't like the usual chunk packets, we update them all across the scope.
        final java.util.List<Player> characters = CharacterLoop.find(shooter.getPosition(), Player.SCENE_RADIUS, Player.class, player -> player.isVisibleInScene(from) && player.isVisibleInScene(to) && (player.getLocation().withinDistance(from, player.getViewDistance()) || player.getLocation().withinDistance(to, player.getViewDistance())));
        for (int i = characters.size() - 1; i >= 0; i--) {
            final Player player = characters.get(i);
            player.getPacketDispatcher().sendProjectile(f, receiver, projectile, speed, offset);
        }
        return projectile.getTime(shooter.getPosition(), to);
    }

    public static void updateEntityChunk(final Entity entity, boolean finish) {
        final boolean player = entity instanceof Player;
        final Location tile = entity.getLocation();
        final int currentChunkId = tile.getChunkHash();
        if (player) {
            GlobalAreaManager.update((Player) entity, false, false);
        }
        if (!finish) {
            entity.checkMultiArea();
        }
        final int lastChunkId = entity.getLastChunkId();
        if (!finish && lastChunkId == currentChunkId) {
            return;
        }
        entity.setLastChunkId(currentChunkId);
        final int regionId = tile.getRegionId();
        if (entity.getLastRegionId() != regionId) {
            entity.setLastRegionId(regionId);
            if (player) {
                ((Player) entity).getMusic().unlock(regionId);
            }
        }
        final Chunk lastChunk = World.getChunk(lastChunkId);
        final Chunk currentChunk = World.getChunk(currentChunkId);
        if (player) {
            final Player p = (Player) entity;
            lastChunk.getPlayers().remove(p);
            if (!finish) currentChunk.getPlayers().add(p);
            if (!p.isTeleported()) {
                p.updateScopeInScene();
            }
        } else {
            final NPC npc = (NPC) entity;
            lastChunk.getNPCs().remove(npc);
            if (!finish) currentChunk.getNPCs().add(npc);
        }
        if (finish) {
            entity.unclip();
        }
    }

    public static Gson getGson() {
        return DefaultGson.getGson();
    }

    public static Location findEmptyNPCSquare(final Location corner, final int size) {
        final int cornerX = corner.getX();
        final int cornerY = corner.getY();
        final int cornerZ = corner.getPlane();
        for (int y = cornerY; y > (cornerY - size); y--) {
            if (!World.isFloorFree(cornerZ, cornerX, y, size)) {
                continue;
            }
            return new Location(cornerX, y, cornerZ);
        }
        return null;
    }

    public static void setMask(@NotNull final Location tile, final int mask) {
        setMask(tile.getPlane(), tile.getX(), tile.getY(), mask);
    }

    public static void setMask(final int plane, final int x, final int y, final int mask) {
        final int regionId = (((x & 16383) >> 6) << 8) | ((y & 16383) >> 6);
        final Region region = World.getRegion(regionId);
        region.setMask(plane & 3, x & 63, y & 63, mask);
    }

    public static boolean checkWalkStep(final int plane, final int x, final int y, final int dir, final int size, final boolean checkCollidingNPCs, final boolean checkCollidingPlayers) {
        return RegionMap.checkWalkStep(plane, x, y, Utils.DIRECTION_DELTA_X[dir], Utils.DIRECTION_DELTA_Y[dir], size, checkCollidingNPCs, checkCollidingPlayers);
    }

    public static boolean containsObjectWithId(final Location tile, final int id) {
        return getRegion(tile.getRegionId()).containsObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), id);
    }

    public static boolean containsObjectWithId(final int x, final int y, final int plane, final int id) {
        return getRegion((((x >> 6) << 8) + (y >> 6))).containsObjectWithId(plane, x & 63, y & 63, id);
    }

    public static WorldObject getObjectWithId(final Location tile, final int id) {
        return getRegion(tile.getRegionId()).getObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), id);
    }

    public static WorldObject getObjectWithType(final Location tile, final int type) {
        return getRegion(tile.getRegionId()).getObjectWithType(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), type);
    }

    public static WorldObject getObjectOfSlot(final Location tile, final int type) {
        return getRegion(tile.getRegionId()).getObjectOfSlot(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), type);
    }

    public static WorldObject getObjectWithType(final int tile, final int type) {
        final int x = (tile >> 14) & 16383;
        final int y = tile & 16383;
        final int z = (tile >> 28) & 3;
        return getRegion((((x >> 6) << 8) + (y >> 6))).getObjectWithType(z, x, y, type);
    }

    public static boolean containsPlayer(final String username) {
        for (final Player p2 : getPlayers()) {
            if (p2 == null || p2.isNulled()) {
                continue;
            }
            if (p2.getPlayerInformation().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * TODO refactor this and all its references.
     * Deprecated for now. No replacement method included.
     */
    @Deprecated
    public static NPC getNPC(final int id, final int... regionIds) {
        /* for (final int regionId : regionIds) {
            final Region region = getRegion(regionId);
            if (region == null) {
                return null;
            }
            final List<NPC> npcIndexes = region.getNPCs();
            if (npcIndexes == null) {
                return null;
            }
            for (final NPC n : npcIndexes) {
                if (n == null) {
                    continue;
                }
                if (n.getId() == id) {
                    return n;
                }
            }
        }*/
        return null;
    }

    public static Optional<NPC> findNPC(final int id, final Location tile) {
        return findNPC(tile, 15, n -> n.getId() == id);
    }

    public static Optional<NPC> findNPC(final int id, final Location tile, final int radius) {
        return findNPC(tile, radius, n -> n.getId() == id);
    }

    public static Optional<NPC> findNPC(final Location tile, final int radius, final Predicate<NPC> predicate) {
        final java.util.List<NPC> npcs = CharacterLoop.find(tile, radius, NPC.class, predicate);
        if (npcs.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(npcs.get(0));
    }

    public static Player getPlayerByDisplayname(final String username) {
        for (final Player player : getPlayers()) {
            if (player == null) {
                continue;
            }
            if (player.getPlayerInformation().getDisplayname().equalsIgnoreCase(username)) {
                return player;
            }
        }
        return null;
    }

    public static Player getPlayerByUsername(final String name) {
        for (final Player player : getPlayers()) {
            if (player == null) {
                continue;
            }
            if (player.getPlayerInformation().getUsername().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    public static EntityList<Player> getPlayers() {
        return PLAYERS;
    }

    public static EntityList<NPC> getNPCs() {
        return NPCS;
    }

    /**
     * Graphically spawns a door at the given location, it's not actually being added to the game. The clipping remains the same.
     *
     * @param object object to spawn
     */
    public static void spawnGraphicalDoor(final WorldObject object) {
        SceneSynchronization.forEach(object, player -> new LocAdd(object));
    }

    /**
     * Graphically removes a door at the given location, it's not actually being removed from the game. The clipping remains the same.
     *
     * @param object object to spawn
     */
    public static void removeGraphicalDoor(final WorldObject object) {
        SceneSynchronization.forEach(object, player -> new LocDel(object));
    }

    public static void spawnObject(final WorldObject object) {
        spawnObject(object, true);
    }

    public static void spawnObject(final WorldObject object, final boolean alterClipping) {
        getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(), object.getYInRegion(), false, alterClipping);
    }

    public static void removeObject(final WorldObject object) {
        if (object == null) {
            return;
        }
        getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(), object.getYInRegion());
    }

    public static final void spawnTemporaryObject(final WorldObject object, final int ticks, final Runnable onRemoval) {
        spawnObject(object);
        WorldTasksManager.schedule(() -> {
            removeObject(object);
            onRemoval.run();
        }, ticks);
    }

    public static void spawnTemporaryObject(final WorldObject object, final int ticks) {
        spawnObject(object);
        WorldTasksManager.schedule(() -> removeObject(object), ticks);
    }

    public static void spawnTemporaryObject(final WorldObject object, final Item item, final int ticks) {
        spawnObject(object);
        WorldTasksManager.schedule(() -> {
            if (World.containsSpawnedObject(object)) {
                removeObject(object);
            }
            World.spawnFloorItem(item, object, null, -1, 100);
        }, ticks);
    }

    public static void spawnTemporaryObject(final WorldObject object, final WorldObject replacement, final int ticks) {
        spawnObject(object);
        WorldTasksManager.schedule(() -> {
            removeObject(object);
            spawnObject(replacement);
        }, ticks);
    }

    public static void sendSoundEffect(final Position tile, final SoundEffect sound) {
        final Location pos = tile.getPosition();
        CharacterLoop.forEach(pos, sound.getRadius(), Player.class, player -> player.getPacketDispatcher().sendAreaSoundEffect(pos, sound));
    }

    public static void sendObjectAnimation(final WorldObject object, final Animation animation) {
        Preconditions.checkArgument(object != null);
        Preconditions.checkArgument(animation != null);
        SceneSynchronization.forEach(object, player -> new LocAnim(object, animation));
    }

    public static final boolean canPlaceObjectWithoutCollisions(@NotNull final Location tile, final int objectType) {
        return canPlaceObjectWithoutCollisions(tile.getX(), tile.getY(), tile.getPlane(), objectType);
    }

    public static final boolean canPlaceObjectWithoutCollisions(final int x, final int y, final int z, final int objectType) {
        return getRegion(Location.getRegionId(x, y), true).getObjectOfSlot(z & 0x3, x & 0x3F, y & 0x3F, objectType) == null;
    }

    /**
     * Updates an existing floor item stack by adding (or removing if {@code amount} is negative) to its stack. Modifies
     * the amount of the
     * FloorItem object itself.
     *
     * @param item   the floor item object to update.
     * @param amount the amount to enqueue or remove from the stack.
     * @param ticks  the amount of ticks the item should remain invisible, if the amount is equal to or below 0, the timer will not be updated.
     */
    private static void updateGroundItem(final FloorItem item, final int amount, final int ticks) {
        final Location tile = item.getLocation();
        final int chunkId = Chunk.getChunkHash(tile.getX() >> 3, tile.getY() >> 3, tile.getPlane());
        final Chunk chunk = World.getChunk(chunkId);
        if (!chunk.getFloorItems().contains(item)) {
            return;
        }
        final int oldQuantity = item.getAmount();
        final int totalAmount = oldQuantity + amount;
        item.setAmount(totalAmount);
        final int invisibleTicks = item.getInvisibleTicks();
        if (invisibleTicks > 0 && ticks > 0) {
            if (invisibleTicks < ticks) {
                item.setInvisibleTicks(ticks);
            }
        }
        SceneSynchronization.forEachFunctional(item.getLocation(), player -> {
            if (!item.isVisibleTo(player) || !player.isFloorItemDisplayed(item)) return;
            player.getPacketDispatcher().sendObjUpdate(item, oldQuantity, tile);
        });
    }

    /**
     * Gets a list of all the floor items on a certain tile.
     *
     * @param player the player who to test, if null, returns only a list of items visible to everyone.
     * @param tile   the tile to check for floor items.
     * @return a list of floor items on the tile, or null or none exist.
     */
    private static List<FloorItem> getFloorItems(final Player player, final Location tile, final boolean invisibleOnly, final boolean includePublicItems) {
        final int chunkId = Chunk.getChunkHash(tile.getX() >> 3, tile.getY() >> 3, tile.getPlane());
        final Chunk chunk = World.getChunk(chunkId);
        final Set<FloorItem> items = chunk.getFloorItems();
        if (items.size() == 0) {
            return null;
        }
        List<FloorItem> list = null;
        for (final FloorItem item : items) {
            if (item == null) {
                continue;
            }
            if (item.getLocation().getPositionHash() != tile.getPositionHash()) {
                continue;
            }
            if (item.isOwner(player) || includePublicItems && !invisibleOnly && item.getInvisibleTicks() <= 0) {
                if (invisibleOnly && item.getInvisibleTicks() <= 0) {
                    continue;
                }
                if (list == null) {
                    list = new ArrayList<>(items.size());
                }
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Turns a floor item public for those who aren't already seeing it and are in range of it.
     *
     * @param item the floor item to turn public.
     */
    private static void turnFloorItemPublic(final FloorItem item) {
        final Location tile = item.getLocation();
        final int chunkId = Chunk.getChunkHash(tile.getX() >> 3, tile.getY() >> 3, tile.getPlane());
        final Chunk chunk = World.getChunk(chunkId);
        if (!chunk.getFloorItems().contains(item)) {
            return;
        }
        SceneSynchronization.forEachFunctional(item.getLocation(), player -> {
            if (item.isReceiver(player) || !player.isFloorItemDisplayed(item) || (item.isVisibleToIronmenOnly() && player.isIronman()))
                return;
            player.getPacketDispatcher().sendObjAdd(item, tile);
        });
    }

    public static FloorItem getFloorItem(final int id, final Location tile, final Player player) {
        return getRegion(tile.getRegionId()).getFloorItem(id, tile, player);
    }

    /**
     * Spawns a floor item for the player, for 100 invisible ticks, and 200 visible ticks.
     *
     * @param item  the item to spawn.
     * @param owner the owner of the item, can be null.
     */
    public static void spawnFloorItem(final Item item, final Player owner, final Location tile) {
        spawnFloorItem(item, tile, -1, owner, owner, 300, 500);
    }

    /**
     * Spawns a floor item for the player, under the player, for 100 invisible ticks, and 200 visible ticks.
     *
     * @param item  the item to spawn.
     * @param owner the owner of the item, cannot be null.
     */
    public static void spawnFloorItem(final Item item, final Player owner) {
        if (owner == null) {
            throw new RuntimeException("The owner of the item cannot be null!");
        }
        spawnFloorItem(item, new Location(owner.getLocation()), -1, owner, owner, 300, 500);
    }

    /**
     * Spawns a floor item for the player, under the player.
     *
     * @param item           the item to spawn.
     * @param owner          the owner of the item, cannot be null.
     * @param invisibleTicks the amount of ticks the item should remain invisible for, if <= 0, spawns as visible for everyone.
     * @param visibleTicks   the amount of ticks the item should remain on the ground for after turning visible.
     */
    public static void spawnFloorItem(final Item item, final Player owner, final int invisibleTicks, final int visibleTicks) {
        if (owner == null) {
            throw new RuntimeException("The owner of the item cannot be null!");
        }
        spawnFloorItem(item, new Location(owner.getLocation()), -1, owner, owner, invisibleTicks, visibleTicks);
    }

    /**
     * Spawns a floor item for the player, if {@code owner} isn't null, if it is, will spawn the floor item for everyone as long as
     * invisible ticks variable is 0 or negative. If that isn't the case, the item will remain invisible until the ticks are up, after which
     * it turns visible for everyone.
     *
     * @param item           the item to spawn.
     * @param tile           the location where to spawn the item.
     * @param owner          the owner of the item, can be null.
     * @param invisibleTicks the amount of ticks the item should remain invisible for, if <= 0, spawns as visible for everyone.
     * @param visibleTicks   the amount of ticks the item should remain on the ground for after turning visible.
     */
    public static void spawnFloorItem(final Item item, final Location tile, final Player owner, final int invisibleTicks, final int visibleTicks) {
        spawnFloorItem(item, tile, -1, owner, owner, invisibleTicks, visibleTicks);
    }

    public static void spawnFloorItem(final Item item, final Location tile, final int maxStack, final Player owner, final Player receiver, int invisibleTicks, int visibleTicks) {
        spawnFloorItem(item, tile, maxStack, owner, receiver, invisibleTicks, visibleTicks, false);
    }

    /**
     * Spawns a floor item for the player, if {@code owner} isn't null, if it is, will spawn the floor item for everyone as long as
     * invisible ticks variable is 0 or negative. If that isn't the case, the item will remain invisible until the ticks are up, after which
     * it turns visible for everyone. If the item is stackable(or noted) and there's already a stack of the item on the ground, it will
     * attempt to update the stack with the amount defined by the item, as long as it's within the restrictions of the {@code maxStack} (if
     * it's -1, no restrictions) and doesn't exceed Integer.MAX_VALUE, and the visibility type is equal to that of the item on the ground.
     *
     * @param item           the item to spawn.
     * @param tile           the location where to spawn the item.
     * @param maxStack       specifically for arrows when ranging, as in OSRS the arrows can only appear up to in stacks of 20, after which a new stack
     *                       is made. Can be used for any stackable item, however there's no other known occurence of it elsewhere.
     * @param owner          the owner of the item, can be null.
     * @param invisibleTicks the amount of ticks the item should remain invisible for, if <= 0, spawns as visible for everyone.
     * @param visibleTicks   the amount of ticks the item should remain on the ground for after turning visible.
     */
    public static void spawnFloorItem(final Item item, final Location tile, final int maxStack, final Player owner, final Player receiver, int invisibleTicks, int visibleTicks, final boolean ironmanOnly) {
        if (owner != null) {
            final Area area = owner.getArea();
            if (area instanceof final DropPlugin plugin) {
                final int invisiblePluginTicks = plugin.invisibleTicks(owner, item);
                final int visiblePluginTicks = plugin.visibleTicks(owner, item);
                if (invisiblePluginTicks != Integer.MIN_VALUE) {
                    invisibleTicks = invisiblePluginTicks;
                }
                if (visiblePluginTicks != Integer.MIN_VALUE) {
                    visibleTicks = visiblePluginTicks;
                }
            }
        }
        if (item.getDefinitions() == null) return;
        if (!item.isTradable()) {
            invisibleTicks += visibleTicks;
            visibleTicks = 0;
        }
        final FloorItem floorItem = new FloorItem(item, new Location(tile), owner, receiver, invisibleTicks, visibleTicks);
        if (ironmanOnly) {
            floorItem.setVisibleToIronmenOnly(true);
        }
        PluginManager.post(new FloorItemSpawnEvent(floorItem));
        final List<FloorItem> items = getFloorItems(owner, tile, true, false);
        final int id = item.getId();
        final ItemDefinitions defs = item.getDefinitions();
        final boolean stackable = defs.isStackable() || defs.isNoted();
        if (stackable && items != null) {
            for (int i = items.size() - 1; i >= 0; i--) {
                final FloorItem it = items.get(i);
                if (it == null || it instanceof GlobalItem) {
                    continue;
                }
                if (invisibleTicks > 0 && it.getInvisibleTicks() <= 0) {
                    continue;
                }
                final int amt = it.getAmount();
                if (amt == Integer.MAX_VALUE || maxStack != -1 && amt >= maxStack) {
                    continue;
                }
                if (it.getId() == id) {
                    if (floorItem.getAmount() + it.getAmount() < 0) {
                        final int amount = Integer.MAX_VALUE - it.getAmount();
                        updateGroundItem(it, amount, invisibleTicks);
                        floorItem.setAmount(floorItem.getAmount() - amount);
                    } else {
                        updateGroundItem(it, floorItem.getAmount(), invisibleTicks);
                        return;
                    }
                }
            }
        }
        final int chunkId = Chunk.getChunkHash(tile.getX() >> 3, tile.getY() >> 3, tile.getPlane());
        final Chunk chunk = World.getChunk(chunkId);
        final int amt = (stackable ? 1 : floorItem.getAmount());
        for (int num = 0; num < amt; num++) {
            final FloorItem fItem = amt == 1 ? floorItem : new FloorItem(new Item(floorItem.getId(), 1), tile, owner, receiver, invisibleTicks, visibleTicks);
            chunk.addFloorItem(fItem);
            SceneSynchronization.forEachFunctional(tile, player -> {
                if (!fItem.isVisibleTo(player) || !player.isFloorItemDisplayed(fItem)) return;
                player.getPacketDispatcher().sendObjAdd(fItem, tile);
            });
        }
    }

    /**
     * Destroys a floor item from a certain location, if a floor item with the same id and amount exists. All attributes MUST match,
     * destroys the first occurrence and stops there.
     *
     * @param item the item to find and destroy.
     * @param tile the tile where the item allegedly is.
     */
    public static void destroyFloorItem(final Item item, final Location tile) {
        destroyFloorItem(null, item, tile);
    }

    /**
     * Destroys a floor item from a certain location, if a floor item with the same id and amount exists. All attributes MUST match,
     * destroys the first occurrence and stops there.
     *
     * @param player the player whose floor item to destroy.
     * @param item   the item to find and destroy.
     * @param tile   the tile where the item allegedly is.
     */
    public static void destroyFloorItem(final Player player, final Item item, final Location tile) {
        final List<FloorItem> items = getFloorItems(player, tile, false, true);
        if (items == null) {
            return;
        }
        final int id = item.getId();
        final int amount = item.getAmount();
        for (int i = items.size() - 1; i >= 0; i--) {
            final FloorItem it = items.get(i);
            if (it == null) {
                continue;
            }
            if (it.getId() != id || it.getAmount() != amount) {
                continue;
            }
            destroyFloorItem(it);
            return;
        }
    }

    /**
     * Destroys a floor item off the ground and updates the occurrence for all nearby players. Does not put the item anywhere. Method is
     * interrupted if the item has already vanished off the ground when it's called.
     *
     * @param item the floor item to destroy.
     */
    public static void destroyFloorItem(final FloorItem item) {
        destroyFloorItem(item, true);
    }

    public static boolean destroyFloorItem(final FloorItem item, final boolean removeFromGlobal) {
        if (item == null) {
            return false;
        }
        final Location tile = item.getLocation();
        final int chunkId = Chunk.getChunkHash(tile.getX() >> 3, tile.getY() >> 3, tile.getPlane());
        final Chunk chunk = World.getChunk(chunkId);
        if (!chunk.getFloorItems().contains(item)) {
            return false;
        }
        chunk.removeFloorItem(item, removeFromGlobal);
        if (item instanceof GlobalItem) {
            ((GlobalItem) item).schedule();
        }
        SceneSynchronization.forEachFunctional(tile, player -> {
            if (!item.isVisibleTo(player) || !player.isFloorItemDisplayed(item)) return;
            player.getPacketDispatcher().sendObjDel(item, tile);
        });
        return true;
    }

    /**
     * Takes the floor item off the ground if possible. Checks for inventory space and possible amount to enqueue to the inventory. If the
     * player cannot pick up all of the item, the item will be updated instead of destroyed (and picked up).
     *
     * @param player the player to give the item to.
     * @param item   the floor item to destroy off the floor and give to the player.
     */
    public static void takeFloorItem(final Player player, final FloorItem item) {
        final Location tile = item.getLocation();
        final int chunkId = Chunk.getChunkHash(tile.getX() >> 3, tile.getY() >> 3, tile.getPlane());
        final Chunk chunk = World.getChunk(chunkId);
        if (!chunk.getFloorItems().contains(item)) {
            player.sendMessage("Too late - It's gone!");
            return;
        }
        if (player.isIronman() && item.hasOwner() && !item.isOwner(player)) {
            player.sendMessage("You're an Iron Man, so you can't take items that other players have dropped.");
            return;
        }
        if (!player.getLocation().matches(item.getLocation())) {
            player.setAnimation(new Animation(832));
        }
        final com.zenyte.game.world.entity.player.container.impl.LootingBag lootingBag = player.getLootingBag();
        final RunePouch runePouch = player.getRunePouch();
        final int id = item.getId();
        final Item it = new Item(id, item.getAmount(), item.isOwner(player) ? item.getAttributesCopy() : null);
        final int amountInRunePouch = runePouch.getAmountOf(id);
        final boolean addToRunePouch = player.getNumericAttribute("put looted runes in rune pouch").intValue() == 1 && player.getInventory().containsItem(12791, 1) && amountInRunePouch > 0 && (amountInRunePouch + it.getAmount()) < 16000;
        final boolean addToQuiver = player.getNumericAttribute("equip ammunition picked up").intValue() == 1 && it.isStackable() && (player.getEquipment().getId(EquipmentSlot.AMMUNITION) == id || player.getEquipment().getId(EquipmentSlot.WEAPON) == id);
        final Container container = addToQuiver ? player.getEquipment().getContainer() : addToRunePouch ? runePouch.getContainer() : player.getInventory().containsItem(LootingBag.OPENED) && lootingBag.isOpen() && player.inArea("Wilderness") && item.isTradable() && !item.getName().startsWith("Mysterious emblem") && !lootingBag.isFull() ? lootingBag.getContainer() : player.getInventory().getContainer();
        final ContainerResult result = container.add(it);
        container.refresh(player);
        final int succeeded = result.getSucceededAmount();
        if (succeeded != it.getAmount()) {
            updateGroundItem(item, -result.getSucceededAmount(), item.getInvisibleTicks());
            player.sendFilteredMessage("Not enough space in your " + container.getType().getName() + " to pick the item up.");
        } else {
            destroyFloorItem(item);
        }
        player.log(LogLevel.INFO, "Taking item '" + item + "'(succeeded count: " + succeeded + ").");
        if (succeeded >= 1) {
            player.sendSound(itemTakeSound);
            if (id == 231) {
                player.getAchievementDiaries().update(FremennikDiary.COLLECT_SNAPE_GRASS);
            } else if (id == 2150) {
                player.getAchievementDiaries().update(WesternProvincesDiary.COLLECT_SWAMP_TOAD);
            } else if (id == 223) {
                player.getAchievementDiaries().update(WildernessDiary.COLLECT_SPIDERS_EGGS);
            }
        }
    }

    public static void sendGraphics(final Graphics graphics, final Location tile) {
        Preconditions.checkArgument(graphics != null);
        Preconditions.checkArgument(tile != null);
        SceneSynchronization.forEach(tile, player -> new MapAnim(tile, graphics));
    }

    public static boolean addPlayer(final Player player) {
        if (PLAYERS.size() >= 2000) {
            return false;
        }
        PLAYERS.add(player);
        NAMED_PLAYERS.put(player.getUsername(), player);
        return true;
    }

    public static void removePlayer(final Player player) {
        PLAYERS.remove(player);
        NAMED_PLAYERS.remove(player.getUsername());
    }

    /**
     * Checks whether the tile's floor and walls are free or not.
     */
    public static boolean isTileFree(final int x, final int y, final int plane, final int size) {
        for (int tileX = x; tileX < x + size; tileX++) {
            for (int tileY = y; tileY < y + size; tileY++) {
                if (getMask(plane, tileX, tileY) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks whether the tile's floor and walls are free or not.
     */
    public static boolean isTileFree(final Location tile, final int size) {
        for (int tileX = tile.getX(); tileX < tile.getX() + size; tileX++) {
            for (int tileY = tile.getY(); tileY < tile.getY() + size; tileY++) {
                if (getMask(tile.getPlane(), tileX, tileY) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isFloorFree(final int plane, final int x, final int y, final int size) {
        for (int tileX = x; tileX < x + size; tileX++) {
            for (int tileY = y; tileY < y + size; tileY++) {
                if (!isFloorFree(plane, tileX, tileY)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isFloorFree(final int plane, final int x, final int y) {
        return (getMask(plane, x, y) & (Flags.FLOOR | Flags.FLOOR_DECORATION | Flags.OBJECT)) == 0;
    }

    public static boolean isWallsFree(final int plane, final int x, final int y) {
        return (getMask(plane, x, y) & (Flags.CORNER_NORTH_EAST | Flags.CORNER_NORTH_WEST | Flags.CORNER_SOUTH_EAST | Flags.CORNER_SOUTH_WEST | Flags.WALL_EAST | Flags.WALL_NORTH | Flags.WALL_SOUTH | Flags.WALL_WEST)) == 0;
    }

    public static boolean isFloorFree(final Location tile, final int size) {
        for (int tileX = tile.getX(); tileX < tile.getX() + size; tileX++) {
            for (int tileY = tile.getY(); tileY < tile.getY() + size; tileY++) {
                if (!isFloorFree(tile.getPlane(), tileX, tileY)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isRegionLoaded(final int regionId) {
        final Region region = getRegion(regionId);
        if (region == null) {
            return false;
        }
        return region.getLoadStage() == 2;
    }

    public static boolean isTileClipped(final Location tile, final int size) {
        return isTileClipped(tile.getPlane(), tile.getX(), tile.getY(), size);
    }

    public static boolean isTileClipped(final int plane, final int x, final int y, final int size) {
        for (int tileX = x; tileX < x + size; tileX++) {
            for (int tileY = y; tileY < y + size; tileY++) {
                if (getMask(plane, tileX, tileY) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getMask(@NotNull final Location tile) {
        return getMask(tile.getPlane(), tile.getX(), tile.getY());
    }

    public static int getMask(final int plane, final int x, final int y) {
        final int regionId = (((x & 16383) >> 6) << 8) | ((y & 16383) >> 6);
        final Region region = World.getRegion(regionId);
        return region.getMask(plane & 3, x & 63, y & 63);
    }

    public static void unregisterPlayer(final Player player) {
        removePlayer(player);
        player.finish();
        CoresManager.getLoginManager().save(player);
    }

    public static Rectangle getRectangle(int x1, int x2, int y1, int y2) {
        if (x2 < x1) {
            final int XI = x1;
            final int XII = x2;
            x1 = XII;
            x2 = XI;
        }
        if (y2 < y1) {
            final int YI = y1;
            final int YII = y2;
            y1 = YII;
            y2 = YI;
        }
        return new Rectangle(x1, y1, (x2 - x1), (y2 - y1));
    }

    /**
     * Starts a dialogue w/ the closest NPC of the id in parameters to the player.
     *
     * @param player   the player who the dialogue is initiated for.
     * @param npcId    the id of the npc to locate.
     * @param radius   the radius from player used to find closest npc.
     * @param dialogue the dialogue to initiate.
     */
    public static void sendClosestNPCDialogue(final Player player, final int npcId, final int radius, final Dialogue dialogue) {
        findNPC(player.getLocation(), radius, npc -> npc.getId() == npcId).ifPresent(npc -> dialogue.setNpc(npc));
        dialogue.setNpcId(npcId);
        player.getDialogueManager().start(dialogue);
    }

    public static void sendClosestNPCDialogue(final Player player, final int npcId, final Dialogue dialogue) {
        sendClosestNPCDialogue(player, npcId, 10, dialogue);
    }

    public static boolean isSpawnedObject(final WorldObject object) {
        final int hash = (object.getX() & 63) | ((object.getY() & 63) << 6) | (Region.OBJECT_SLOTS[object.getType()] << 12) | (object.getPlane() << 14);
        final WorldObject obj = getRegion(object.getRegionId()).getObjects().get((short) hash);
        return obj == null || !obj.equals(object);
    }

    public static boolean isMultiArea(final Location tile) {
        return MultiwayArea.isMultiArea(tile);
    }

    public static void shufflePids() {
        final int n = 2000;
        for (int i = 0; i < n; i++) {
            final int change = i + Utils.SECURE_RANDOM.nextInt(n - i);
            swap(i, change);
        }
    }

    private static void swap(final int i, final int change) {
        final Player a = World.USED_PIDS.get(i);
        final Player b = World.USED_PIDS.get(change);
        if ((a == null || a.getArea() instanceof ArenaArea) && (b == null || b.getArea() instanceof ArenaArea)) {
            return;
        }
        final int pidA = a == null ? -1 : a.getPid();
        final int pidB = b == null ? -1 : b.getPid();
        if (pidA != -1 && pidB != -1) {
            World.USED_PIDS.put(pidB, a);
            World.USED_PIDS.put(pidA, b);
            b.setPid(pidA);
            a.setPid(pidB);
        } else if (pidA != -1) {
            final int pid = World.AVAILABLE_PIDS.removeInt(Utils.random(World.AVAILABLE_PIDS.size() - 1));
            a.setPid(pid);
            World.USED_PIDS.put(pid, a);
            World.USED_PIDS.remove(pidA);
            World.AVAILABLE_PIDS.add(pidA);
        } else if (pidB != -1) {
            final int pid = World.AVAILABLE_PIDS.removeInt(Utils.random(World.AVAILABLE_PIDS.size() - 1));
            b.setPid(pid);
            World.USED_PIDS.put(pid, b);
            World.USED_PIDS.remove(pidB);
            World.AVAILABLE_PIDS.add(pidB);
        }
    }

    public static boolean isUpdating() {
        return World.updating;
    }

    public static void setUpdating(final boolean updating) {
        World.updating = updating;
    }

    public static int getUpdateTimer() {
        return World.updateTimer;
    }

    public static void setUpdateTimer(final int updateTimer) {
        World.updateTimer = updateTimer;
    }

    public static Set<FloorItem> getAllFloorItems() {
        return World.allFloorItems;
    }
}
