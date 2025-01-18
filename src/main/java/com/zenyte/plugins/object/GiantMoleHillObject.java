package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.GlobalAreaManager;

import java.util.Set;

/**
 * @author Tommeh | 02/05/2019 | 18:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GiantMoleHillObject implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Look-inside")) {
            final Set<Player> players = GlobalAreaManager.get("Falador Mole Lair").getPlayers();
            final int playerCount = players.size();
            player.sendMessage("You look inside the mole hill and see " + (playerCount == 0 ? "no adventurers" : playerCount == 1 ? "1 adventurer" : playerCount + " adventurers") + " inside the mole tunnels.");
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { ObjectId.MOLE_HILL };
    }
}
