package com.zenyte.plugins.drop;

import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.npc.impl.slayer.superior.SuperiorMonster;

/**
 * @author Kris | 12/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SuperiorProcessor extends DropProcessor {
    @Override
    public void attach() {
        //Actual drops are being dropped through the NPC class itself.
        for (int id : allIds) {
            final int req = NPCCDLoader.get(id).getSlayerLevel();
            final double probability = 1.0F / (1.0F / (200.0F - (Math.pow(req + 55.0F, 2) / 125.0F)));
            appendDrop(new DisplayedDrop(20736, 1, 1, probability / (3 / 8.0F), (player, npcId) -> npcId == id));
            appendDrop(new DisplayedDrop(20730, 1, 1, probability / (3 / 8.0F), (player, npcId) -> npcId == id));
            appendDrop(new DisplayedDrop(20724, 1, 1, probability / (1 / 8.0F), (player, npcId) -> npcId == id));
            appendDrop(new DisplayedDrop(21270, 1, 1, probability / (1 / 8.0F), (player, npcId) -> npcId == id));
            put(id, 20736, new PredicatedDrop("Superior monsters will always roll three times on the parent NPC's drop table in addition to rolling once on the drops shown here."));
        }
    }

    @Override
    public int[] ids() {
        return SuperiorMonster.superiorMonsters.toIntArray();
    }
}
