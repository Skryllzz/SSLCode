package com.zenyte.game.content.minigame.warriorsguild.plugins;

import com.zenyte.game.content.minigame.warriorsguild.kegbalance.KegBalanceArea;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.NullObjectID;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 23/03/2019 17:10
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Keg implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equalsIgnoreCase("Pick-up")) {
            final int kegs = player.getNumericTemporaryAttribute("WG Kegs Count").intValue();
            if (KegBalanceArea.pickKeg(player, object, kegs)) {
                player.getTemporaryAttributes().put("WG Kegs Count", kegs + 1);
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { NullObjectID.NULL_15669, NullObjectID.NULL_15670, NullObjectID.NULL_15671, NullObjectID.NULL_15672, NullObjectID.NULL_15673 };
    }
}
