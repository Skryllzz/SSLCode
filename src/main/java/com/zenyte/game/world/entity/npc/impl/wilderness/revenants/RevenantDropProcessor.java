package com.zenyte.game.world.entity.npc.impl.wilderness.revenants;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import mgi.types.config.npcs.NPCDefinitions;

/**
 * @author Kris | 18/04/2019 19:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RevenantDropProcessor extends DropProcessor {
    @Override
    public void attach() {
        final int[] ids = allIds;
        final Revenant.GoodRevenantDrop[] goodDrops = Revenant.GoodRevenantDrop.values();
        final Revenant.MediocreReventantDrop[] mediocreDrops = Revenant.MediocreReventantDrop.values();
        for (final int id : ids) {
            final int level = NPCDefinitions.getOrThrow(id).getCombatLevel();
            final int clampedLevel = Math.max(1, Math.min(144, level));
            final int chanceA = 2200 / ((int) Math.sqrt(clampedLevel));
            final int chanceB = 15 + ((int) Math.pow(level + 60.0F, 2) / 200);
            for (final Revenant.GoodRevenantDrop drop : goodDrops) {
                if (drop == Revenant.GoodRevenantDrop.REVENANT_WEAPON) {
                    final int weight = chanceA * 40;
                    appendDrop(new DisplayedDrop(22557, 1, 1, (int) (weight * 2.5F), (p, npcId) -> npcId == id, id));
                    appendDrop(new DisplayedDrop(22542, 1, 1, weight * 5, (p, npcId) -> npcId == id, id));
                    appendDrop(new DisplayedDrop(22547, 1, 1, weight * 5, (p, npcId) -> npcId == id, id));
                    appendDrop(new DisplayedDrop(22552, 1, 1, weight * 5, (p, npcId) -> npcId == id, id));
                    put(id, 22557, new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format((int) ((chanceA * 14) * 2.5F)) + " while skulled."));
                    put(id, 22542, new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format((int) ((chanceA * 14) * 5.0F)) + " while skulled."));
                    put(id, 22547, new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format((int) ((chanceA * 14) * 5.0F)) + " while skulled."));
                    put(id, 22552, new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format((int) ((chanceA * 14) * 5.0F)) + " while skulled."));
                    continue;
                }
                if (drop.ordinal() <= Revenant.GoodRevenantDrop.ANCIENT_CRYSTAL.ordinal()) {
                    if (drop == Revenant.GoodRevenantDrop.ANCIENT_CRYSTAL) {
                        final int weight = chanceA * 14;
                        put(id, drop.getItem().getId(), new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is decreased to 1 / " + Utils.format(weight) + " while skulled."));
                    } else {
                        final int weight = chanceA * 14 / (1 + drop.getRange().getEndInclusive() - drop.getRange().getStart());
                        if (!getInfoMap().containsKey(drop.getItem().getId())) {
                            put(id, drop.getItem().getId(), new PredicatedDrop((p, npcId) -> npcId == id, "Drop rate is increased to 1 / " + Utils.format(weight) + " while skulled."));
                        }
                    }
                }
                final int weight = chanceA * 40 / ((drop.getRange().getEndInclusive() - drop.getRange().getStart()) + 1);
                appendDrop(new DisplayedDrop(drop.getItem().getId(), drop.getItem().getMinAmount(), drop.getItem().getMaxAmount(), weight, (p, npcId) -> npcId == id, id));
            }
            for (final Revenant.MediocreReventantDrop drop : mediocreDrops) {
                final int weight = (int) ((float) chanceA / (chanceB - 1) * 106.0F / drop.getWeight());
                appendDrop(new DisplayedDrop(drop.getItem().getId(), drop.getItem().getMinAmount(), drop.getItem().getMaxAmount(), weight, (p, npcId) -> npcId == id, id));
            }
            appendDrop(new DisplayedDrop(21817, 1, 1, (int) ((float) chanceA / (chanceB - 1) * 106.0F / 15), (p, npcId) -> npcId == id, id));
            appendDrop(new DisplayedDrop(21820, 1, Math.max(1, (int) Math.sqrt(level * 3)), 1, (p, npcId) -> npcId == id, id));
            final float coinsChance = (float) chanceA / (chanceA - chanceB);
            if (coinsChance > 0) {
                appendDrop(new DisplayedDrop(995, 1, 100, coinsChance, (p, npcId) -> npcId == id, id));
            }
        }
    }

    @Override
    public int[] ids() {
        final IntOpenHashSet set = new IntOpenHashSet();
        set.add(7881);
        for (int i = 7931; i <= 7940; i++) {
            set.add(i);
        }
        return set.toIntArray();
    }
}
