package com.zenyte.plugins.events;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;

public final class PostWindowStatusEvent implements Event {
    private final Player player;

    public PostWindowStatusEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
