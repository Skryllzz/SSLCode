package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

import java.util.ArrayList;

/**
 * @author Kris | 30/11/2018 18:44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BarrowsStaircase implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Climb-up")) {
            final BarrowsWight wight = Utils.findMatching(BarrowsWight.values, npc -> npc.getStaircaseId() == object.getId());
            if (wight != null) {
                player.setLocation(wight.getMoundCenter());
            }
        }
    }

    @Override
    public Object[] getObjects() {
        final ArrayList<Integer> list = new ArrayList<>();
        for (final BarrowsWight wight : BarrowsWight.values) {
            list.add(wight.getStaircaseId());
        }
        return list.toArray();
    }
}
