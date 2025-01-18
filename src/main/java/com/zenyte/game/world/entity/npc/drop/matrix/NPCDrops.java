package com.zenyte.game.world.entity.npc.drop.matrix;

import com.zenyte.Constants;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mgi.types.config.items.ItemDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class NPCDrops {
    private static final Logger log = LoggerFactory.getLogger(NPCDrops.class);
    public static Int2ObjectOpenHashMap<DropTable> drops;
    public static Int2ObjectOpenHashMap<DisplayedDropTable> displayedDrops;
    public static Int2ObjectOpenHashMap<List<ItemDrop>> dropsByItem;

    public static Drop[] getDrops(final int npcId) {
        final NPCDrops.DropTable table = drops.get(npcId);
        if (table == null) return null;
        return table.drops;
    }

    public static DropTable getTable(final int npcId) {
        return drops.get(npcId);
    }

    public static List<ItemDrop> getTableForItem(final int itemId) {
        return dropsByItem.get(itemId);
    }

    public static boolean equalsIgnoreRates(final int npc1, final int npc2) {
        final List<DropProcessor> processorA = DropProcessorLoader.get(npc1);
        final List<DropProcessor> processorB = DropProcessorLoader.get(npc2);
        final NPCDrops.DropTable tableA = drops.get(npc1);
        final NPCDrops.DropTable tableB = drops.get(npc2);
        if (tableA == tableB && Objects.equals(processorA, processorB)) {
            return true;
        }
        if (tableA == null || tableB == null) {
            return false;
        }
        if (tableA.drops.length != tableB.drops.length) {
            return false;
        }
        for (int i = 0; i < tableA.drops.length; i++) {
            final Drop drop = tableA.drops[i];
            if (Utils.findMatching(tableB.drops, d -> d.getItemId() == drop.getItemId() && d.getMinAmount() == drop.getMinAmount() && d.getMaxAmount() == drop.getMaxAmount()) != null) {
                continue;
            }
            return false;
        }
        return Objects.equals(processorA, processorB);
    }

    /**
     * Do not make a subscribable event out of this as it needs to be executed before those.
     */
    public static void init() {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader("data/npcs/drops.json"));
            final NPCDrops.DropTable[] definitions = World.getGson().fromJson(reader, DropTable[].class);
            drops = new Int2ObjectOpenHashMap<>((int) Math.ceil(definitions.length / 0.75F));
            displayedDrops = new Int2ObjectOpenHashMap<>((int) Math.ceil(definitions.length / 0.75F));
            dropsByItem = new Int2ObjectOpenHashMap<>(ItemDefinitions.definitions.length);
            for (final NPCDrops.DropTable definition : definitions) {
                int weight = 0;
                for (final Drop drop : definition.getDrops()) {
                    if (drop.getRate() == 100000) {
                        continue;
                    }
                    weight += drop.getRate();
                }
                Arrays.sort(definition.getDrops(), Comparator.comparingInt(Drop::getRate));
                definition.weight = weight;
                drops.put(definition.npcId, definition);
                final ObjectArrayList<NPCDrops.DisplayedNPCDrop> dropsList = new ObjectArrayList<>();
                for (final Drop drop : definition.drops) {
                    if (drop.getItemId() == ItemId.TOOLKIT) {
                        continue;
                    }
                    dropsList.add(new DisplayedNPCDrop(drop.getItemId(), drop.getMinAmount(), drop.getMaxAmount(), drop.getRate(), weight));
                }
                final NPCDrops.DisplayedDropTable clone = new DisplayedDropTable(definition.npcId, dropsList);
                displayedDrops.put(definition.npcId, clone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the drops, sending all of the "always" drops and picking one random drop.
     *
     * @param drops    the droptable.
     * @param consumer the consumer executed on the lucky drops.
     */
    public static void forEach(final DropTable drops, final Consumer<Drop> consumer) {
        final int weight = drops.getWeight();
        final int randomRate = Utils.random(Math.max(100000, weight));
        final Drop[] array = drops.getDrops();
        int currentWeight = 0;
        for (int i = array.length - 1; i >= 0; i--) {
            final Drop drop = array[i];
            final int rate = drop.getRate();
            if (rate == 100000) {
                consumer.accept(drop);
                continue;
            }
            if ((currentWeight += rate) >= randomRate) {
                //Treat toolkits as if they're "nothing".
                if (drop.getItemId() == ItemId.TOOLKIT) {
                    return;
                }
                consumer.accept(drop);
                return;
            }
        }
    }

    public void save() {
        if (!Constants.WORLD_PROFILE.isDevelopment()) {
            throw new IllegalStateException("Saving drops may only be done on development worlds as it reflects on the actual in-use drops.");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("data/npcs/drops.json")))) {
            writer.write(World.getGson().toJson(new ArrayList<>(drops.values())));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final class DropTable {
        private final Drop[] drops;
        private int npcId;
        private transient int weight;

        public DropTable(final int npcId, final int weight, final Drop[] drops) {
            this.npcId = npcId;
            this.weight = weight;
            this.drops = drops;
        }

        public int getNpcId() {
            return this.npcId;
        }

        public void setNpcId(final int npcId) {
            this.npcId = npcId;
        }

        public int getWeight() {
            return this.weight;
        }

        public Drop[] getDrops() {
            return this.drops;
        }
    }

    public static final class DisplayedNPCDrop {
        private final int itemId;
        private final int minAmount;
        private final int maxAmount;
        private final BiFunction<Player, Integer, Double> function;
        private BiPredicate<Player, Integer> predicate;

        public DisplayedNPCDrop(final int itemId, final int minAmount, final int maxAmount, final double weight, final int tableWeight) {
            this.itemId = itemId;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.function = (player, npcId) -> weight == 100000 ? 1 : (weight / tableWeight * 100.0);
        }

        public DisplayedNPCDrop(final int itemId, final int minAmount, final int maxAmount, final BiFunction<Player, Integer, Double> function) {
            this.itemId = itemId;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.function = function;
        }

        public int getItemId() {
            return this.itemId;
        }

        public int getMinAmount() {
            return this.minAmount;
        }

        public int getMaxAmount() {
            return this.maxAmount;
        }

        public BiFunction<Player, Integer, Double> getFunction() {
            return this.function;
        }

        public BiPredicate<Player, Integer> getPredicate() {
            return this.predicate;
        }

        public void setPredicate(final BiPredicate<Player, Integer> predicate) {
            this.predicate = predicate;
        }
    }

    public static final class DisplayedDropTable {
        private final List<DisplayedNPCDrop> drops;
        private int npcId;

        public DisplayedDropTable(final int npcId, final List<DisplayedNPCDrop> drops) {
            this.npcId = npcId;
            this.drops = drops;
        }

        public int getNpcId() {
            return this.npcId;
        }

        public void setNpcId(final int npcId) {
            this.npcId = npcId;
        }

        public List<DisplayedNPCDrop> getDrops() {
            return this.drops;
        }
    }
}
