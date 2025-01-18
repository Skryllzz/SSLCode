package com.zenyte.game.world.flooritem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.item.Item;
import com.zenyte.game.packet.out.ObjAdd;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Chunk;
import com.zenyte.game.world.region.Region;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Kris | 30. mai 2018 : 01:44:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class GlobalItem extends FloorItem {
    private static final Logger log = LoggerFactory.getLogger(GlobalItem.class);
    private static final Int2ObjectOpenHashMap<Set<GlobalItem>> GLOBAL_ITEMS = new Int2ObjectOpenHashMap<>();
    private static final Queue<GlobalItem> PENDING_GLOBAL_ITEMS = new ConcurrentLinkedQueue<>();
    private int respawnTime;
    private int originalAmount;
    private transient int ticks;

    public GlobalItem(final Item item, final Location location, final int respawnTime) {
        super(item, location, null, null, -1, -1);
        this.respawnTime = respawnTime;
        originalAmount = item.getAmount();
    }

    public static void createPersistentGlobalItemSpawn(final GlobalItem item) {
        final int regionId = item.getLocation().getRegionId();
        if (!GLOBAL_ITEMS.containsKey(regionId)) {
            GLOBAL_ITEMS.put(regionId, new LinkedHashSet<>());
        }
        final Set<GlobalItem> set = GLOBAL_ITEMS.get(regionId);
        set.add(item);
    }

    public static Set<GlobalItem> getGlobalItems(final int regionId) {
        return GLOBAL_ITEMS.get(regionId);
    }

    public static void load() {
        WorldTasksManager.schedule(() -> {
            try {
                if (PENDING_GLOBAL_ITEMS.isEmpty()) {
                    return;
                }
                final Iterator<GlobalItem> iterator = PENDING_GLOBAL_ITEMS.iterator();
                while (iterator.hasNext()) {
                    try {
                        final GlobalItem item = iterator.next();
                        if (--item.ticks == 0) {
                            item.spawn();
                            iterator.remove();
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }, 0, 0);
        CoresManager.getServiceProvider().executeNow(() -> {
            try {
                final BufferedReader br = new BufferedReader(new FileReader("data/items/globalItems.json"));
                final GlobalItem.SkeletonGlobalItem[] definitions = World.getGson().fromJson(br, SkeletonGlobalItem[].class);
                for (final GlobalItem.SkeletonGlobalItem def : definitions) {
                    final Location tile = new Location(def.x, def.y, def.z);
                    final GlobalItem item = new GlobalItem(new Item(def.id, def.amount), tile, def.respawnTime);
                    createPersistentGlobalItemSpawn(item);
                }
            } catch (final Exception e) {
                log.error("", e);
            }
        });
    }

    public static void parse() {
        String line = "";
        int i = 0;
        final ArrayList<GlobalItem.SkeletonGlobalItem> globalItems = new ArrayList<>();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("Ground item drops.txt")))) {
            while ((line = reader.readLine()) != null) {
                i++;
                if (!line.startsWith("item name:") && !line.startsWith("Item name:")) {
                    continue;
                }
                final int index = line.indexOf("item id:");
                line = line.substring(index + 8);
                line = line.trim();
                final String[] split = line.split(" ");
                final Integer id = Integer.valueOf(split[0].substring(split[0].indexOf("(") + 1, split[0].length() - 1));
                final Integer x = Integer.valueOf(split[3].substring(0, split[3].length() - 1));
                final Integer y = Integer.valueOf(split[4].substring(0, split[4].length() - 1));
                final Integer z = Integer.valueOf(split[5]);
                int amount = 1;
                if (line.contains("amount: ")) {
                    final int startIndex = line.indexOf("amount: ");
                    final String restOfTheLine = line.substring(startIndex);
                    final int endIndex = restOfTheLine.indexOf(")") + startIndex;
                    amount = Integer.valueOf(line.substring(startIndex + 8, endIndex));
                }
                int respawnTime = 30;
                if (line.contains("respawn time: ")) {
                    final int startIndex = line.indexOf("respawn time: ");
                    final String restOfTheLine = line.substring(startIndex);
                    final int endIndex = restOfTheLine.indexOf(" second") + startIndex;
                    respawnTime = Integer.valueOf(line.substring(startIndex + 14, endIndex));
                }
                globalItems.add(new SkeletonGlobalItem(id, amount, x, y, z, respawnTime));
            }
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println(line + " --------- " + i);
        }
        final String toJson = gson.toJson(globalItems);
        try {
            final PrintWriter pw = new PrintWriter("globalItems.json", StandardCharsets.UTF_8);
            pw.println(toJson);
            pw.close();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public void schedule() {
        ticks = respawnTime;
        setAmount(originalAmount);
        PENDING_GLOBAL_ITEMS.add(this);
    }

    public void spawn() {
        final int regionId = location.getRegionId();
        final Region region = World.getRegion(regionId);
        if (region == null) {
            return;
        }
        final int chunkId = Chunk.getChunkHash(location.getX() >> 3, location.getY() >> 3, location.getPlane());
        final Chunk chunk = World.getChunk(chunkId);
        chunk.addFloorItem(this);
        for (final Player player : World.getPlayers()) {
            if (player == null || !player.getMapRegionsIds().contains(regionId)) {
                continue;
            }
            player.sendZoneUpdate(location.getX(), location.getY(), new ObjAdd(this));
        }
    }

    public int getRespawnTime() {
        return this.respawnTime;
    }

    public void setRespawnTime(final int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public int getOriginalAmount() {
        return this.originalAmount;
    }

    public void setOriginalAmount(final int originalAmount) {
        this.originalAmount = originalAmount;
    }

    public static class SkeletonGlobalItem {
        private int id;
        private int amount;
        private int x;
        private int y;
        private int z;
        private int respawnTime;

        public SkeletonGlobalItem(final int id, final int amount, final int x, final int y, final int z, final int respawnTime) {
            this.id = id;
            this.amount = amount;
            this.x = x;
            this.y = y;
            this.z = z;
            this.respawnTime = respawnTime;
        }

        public SkeletonGlobalItem() {
        }

        public int getId() {
            return this.id;
        }

        public int getAmount() {
            return this.amount;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        public int getRespawnTime() {
            return this.respawnTime;
        }
    }
}
