package com.zenyte.game.item;

import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 10. nov 2017 : 23:12.29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public interface ItemOnObjectAction {

    void handleItemOnObjectAction(final Player player, final Item item, int slot, final WorldObject object);

    Object[] getItems();

    Object[] getObjects();

    default void handle(final Player player, final Item item, int slot, final WorldObject object) {
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object), () -> {
            player.stopAll();
            player.faceObject(object);
            if (player.getInventory().getItem(slot) != item) {
                return;
            }
            handleItemOnObjectAction(player, item, slot, object);
        }));
    }

}
