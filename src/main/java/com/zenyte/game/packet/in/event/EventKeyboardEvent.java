package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class EventKeyboardEvent implements ClientProtEvent {
    private final int key;
    private final int msLastKeyStroke;

    public EventKeyboardEvent(final int key, final int msLastKeyStroke) {
        this.key = key;
        this.msLastKeyStroke = msLastKeyStroke;
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Key: " + key + ", time: " + msLastKeyStroke);
    }

    @Override
    public void handle(Player player) {
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
