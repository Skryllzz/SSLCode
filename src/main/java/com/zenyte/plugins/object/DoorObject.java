package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.DoorHandler;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 10. nov 2017 : 22:01.53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class DoorObject implements ObjectAction {

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (option.equals("Open") || option.equals("Close")) {
            DoorHandler.handleDoor(object);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { ObjectId.TOWER_DOOR, ObjectId.GATE_2623, ObjectId.GATE_21600, "Door", ObjectId.GATE_3444, ObjectId.GATE_3445, "Bamboo Door", ObjectId.STRANGE_WALL_4545, ObjectId.STRANGE_WALL_4546, ObjectId.WALL_2606, ObjectId.GATE_9141, ObjectId.LARGE_DOOR_17089, ObjectId.LARGE_DOOR_1517 };
    }
}
