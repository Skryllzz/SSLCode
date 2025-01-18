package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Tommeh | 23-11-2018 | 23:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class tumeguardianD extends Dialogue {
    public tumeguardianD(Player player, int npcId) {
        super(player, npcId);
    }

    @Override
    public void buildDialogue() {
        final int random = Utils.random(4);
        switch (random) {
            case 0:
                player("So how are you doing?");
                npc("We walk in the light of Tumeken. Gone he may be, but his fire burns on through all who remain. For some, the fire heals. For others, it destroys. This is as it should be. It is all part of the plan.");
                player("Okay... never mind.");
                break;



        }
    }
}
