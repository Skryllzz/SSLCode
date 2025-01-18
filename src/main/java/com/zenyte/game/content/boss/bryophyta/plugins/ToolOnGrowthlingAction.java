package com.zenyte.game.content.boss.bryophyta.plugins;

import com.zenyte.game.content.skills.woodcutting.AxeDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * @author Tommeh | 17/05/2019 | 20:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ToolOnGrowthlingAction implements ItemOnNPCAction {

    @Override
    public void handleItemOnNPCAction(Player player, Item item, int slot, NPC npc) {
        if (!npc.isDead() && npc.getHitpoints() <= 2) {
            npc.sendDeath();
        } else {
            player.sendMessage("The growthling isn't weak enough yet.");
        }
    }

    @Override
    public Object[] getItems() {
        final IntArrayList list = new IntArrayList();
        for (final AxeDefinitions def : AxeDefinitions.VALUES) {
            list.add(def.getItemId());
        }
        list.add(5329);
        list.add(7409);
        return list.toArray(new Object[list.size()]);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { NpcId.GROWTHLING };
    }
}
