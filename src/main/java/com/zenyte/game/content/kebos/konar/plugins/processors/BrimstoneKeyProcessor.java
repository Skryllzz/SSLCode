package com.zenyte.game.content.kebos.konar.plugins.processors;

import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.content.skills.slayer.SlayerMaster;
import com.zenyte.game.content.skills.slayer.Task;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Area;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import mgi.types.config.npcs.NPCDefinitions;

import java.util.Arrays;
import java.util.List;

/**
 * @author Tommeh | 15/10/2019 | 10:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class BrimstoneKeyProcessor extends DropProcessor {
    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(ItemId.BRIMSTONE_KEY, 1, 1) {
            public double getRate(final Player player, int id) {
                if (id == 8058) {
                    //vorkath
                    id = 8060;
                }
                final NPCDefinitions definitions = NPCDefinitions.get(id);
                final int combatLevel = definitions.getCombatLevel();
                double rate = 0;
                if (combatLevel >= 100) {
                    rate = -0.2 * Math.min(combatLevel, 350) + 120;
                } else {
                    rate = Math.pow(0.2 * (combatLevel - 100), 2) + 100;
                }
                return rate;
            }
        });
        put(ItemId.BRIMSTONE_KEY, new PredicatedDrop("This drop is can only be obtained when killing the monster during a slayer assignment, assigned by Konar quo Maten."));
    }

    @Override
    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
        if (killer.getSlayer().getAssignment() != null) {
            final Class<? extends Area> area = killer.getSlayer().getAssignment().getArea();
            if (!drop.isAlways() && killer.getSlayer().isCurrentAssignment(npc) && killer.getSlayer().getAssignment().getMaster().equals(SlayerMaster.KONAR_QUO_MATEN) && area != null && killer.inArea(area)) {
                if (random((int) getBasicDrops().get(0).getRate(killer, npc.getId())) == 0) {
                    return new Item(ItemId.BRIMSTONE_KEY);
                }
            }
        }
        return item;
    }

    @Override
    public int[] ids() {
        final IntSet list = IntSets.synchronize(new IntOpenHashSet(100));
        final List<NPCDefinitions> definitionsList = Arrays.asList(NPCDefinitions.definitions);
        definitionsList.parallelStream().forEach(definition -> {
            if (definition == null) {
                return;
            }
            final String name = definition.getName().toLowerCase();
            for (final RegularTask task : RegularTask.VALUES) {
                for (final Task t : task.getTaskSet()) {
                    if (t.getSlayerMaster().equals(SlayerMaster.KONAR_QUO_MATEN)) {
                        for (final String monster : task.getMonsters()) {
                            if (monster.equals(name)) {
                                list.add(definition.getId());
                            }
                        }
                    }
                }
            }
        });
        return list.toIntArray();
    }
}
