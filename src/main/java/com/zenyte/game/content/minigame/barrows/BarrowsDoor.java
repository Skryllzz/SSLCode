package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.*;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kris | 5. dets 2017 : 0:22.29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BarrowsDoor implements ObjectAction {

    private static final Logger log = LoggerFactory.getLogger(BarrowsDoor.class);

    private static final int[] puzzleDoorTiles = new int[] { Location.hash(3551, 9683, 0), Location.hash(3552, 9683, 0), Location.hash(3540, 9695, 0), Location.hash(3540, 9694, 0), Location.hash(3552, 9706, 0), Location.hash(3551, 9706, 0), Location.hash(3563, 9694, 0), Location.hash(3563, 9695, 0) };

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (ArrayUtils.contains(puzzleDoorTiles, player.getLocation().getPositionHash())) {
            if (!player.getBarrows().isPuzzleSolved()) {
                player.sendMessage("The door is locked with a strange puzzle.");
                player.getBarrows().getPuzzle().reset();
                GameInterface.BARROWS_PUZZLE.open(player);
                return;
            }
        }
        TemporaryDoubleDoor.executeBarrowsDoors(player, object, location -> player.getBarrows().sendRandomTarget(location));
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { ObjectId.DOOR_20679, ObjectId.DOOR_20680, NullObjectID.NULL_20681, NullObjectID.NULL_20682, NullObjectID.NULL_20683, NullObjectID.NULL_20684, NullObjectID.NULL_20685, NullObjectID.NULL_20686, NullObjectID.NULL_20687, NullObjectID.NULL_20688, NullObjectID.NULL_20689, NullObjectID.NULL_20690, NullObjectID.NULL_20691, NullObjectID.NULL_20692, NullObjectID.NULL_20693, NullObjectID.NULL_20694, NullObjectID.NULL_20695, NullObjectID.NULL_20696, ObjectId.DOOR_20698, ObjectId.DOOR_20699, NullObjectID.NULL_20700, NullObjectID.NULL_20701, NullObjectID.NULL_20702, NullObjectID.NULL_20703, NullObjectID.NULL_20704, NullObjectID.NULL_20705, NullObjectID.NULL_20706, NullObjectID.NULL_20707, NullObjectID.NULL_20708, NullObjectID.NULL_20709, NullObjectID.NULL_20710, NullObjectID.NULL_20711, NullObjectID.NULL_20712, NullObjectID.NULL_20713, NullObjectID.NULL_20714, NullObjectID.NULL_20715, NullObjectID.NULL_20717 };
    }
}
