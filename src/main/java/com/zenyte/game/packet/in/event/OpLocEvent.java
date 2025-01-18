package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpLocEvent implements ClientProtEvent {
    private final int id;
    private final int x;
    private final int y;
    private final int option;
    private final boolean run;

    public OpLocEvent(final int id, final int x, final int y, final int option, final boolean run) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.option = option;
        this.run = run;
    }

    @Override
    public void handle(Player player) {
        ObjectHandler.handle(player, id, new Location(x, y, player.getPlane()), run, option);
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Id: " + id + ", x: " + x + ", y: " + y + ", z: " + player.getPlane() + ", option: " + option + ", run: " + run);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
