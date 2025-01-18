package com.zenyte.game.content.chambersofxeric.map;

import com.zenyte.game.util.Direction;

/**
 * @author Kris | 21/09/2019 22:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
final class LayoutRoom {
    private final RaidRoom room;
    private final Direction direction;

    public LayoutRoom(final RaidRoom room, final Direction direction) {
        this.room = room;
        this.direction = direction;
    }

    public RaidRoom getRoom() {
        return this.room;
    }

    public Direction getDirection() {
        return this.direction;
    }
}
