package com.zenyte.game.content.skills.hunter;

import com.zenyte.game.util.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Kris | 25/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum BirdHousePosition {
    NORTH_WEST(30565),
    EAST(30566),
    SOUTH_EAST(30567),
    SOUTH(30568);
    private static final List<BirdHousePosition> values = Collections.unmodifiableList(Arrays.asList(values()));
    private final int objectId;

    BirdHousePosition(final int objectId) {
        this.objectId = objectId;
    }

    public static Optional<BirdHousePosition> findPosition(final int objectId) {
        return Optional.ofNullable(Utils.findMatching(values, value -> value.objectId == objectId));
    }

    public static List<BirdHousePosition> getValues() {
        return BirdHousePosition.values;
    }

    public int getObjectId() {
        return this.objectId;
    }
}
