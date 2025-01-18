package com.zenyte.game.world.entity.player;

/**
 * @author Tommeh | 4-2-2019 | 22:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum MessageType {
    UNFILTERABLE(0),
    GLOBAL_BROADCAST(14),
    EXAMINE_ITEM(27),
    EXAMINE_NPC(28),
    EXAMINE_OBJECT(29),
    AUTOTYPER(90),
    TRADE_REQUEST(101),
    CHALLENGE_REQUEST(103),
    FILTERABLE(105);
    private final int type;

    MessageType(final int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
