package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class PlayerReportEvent implements ClientProtEvent {
    private final String name;
    private final int rule;
    private final boolean mute;

    public PlayerReportEvent(final String name, final int rule, final boolean mute) {
        this.name = name;
        this.rule = rule;
        this.mute = mute;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Name: " + name + ", rule: " + rule + ", mute: " + mute);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
    }
}
