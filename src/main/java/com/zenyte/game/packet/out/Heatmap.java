package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 04/03/2019 00:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Heatmap implements GamePacketEncoder {
    private final boolean enabled;

    public Heatmap(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Enabled: " + enabled);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.HEAT_MAP;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeByte(enabled ? 1 : 0);
        return new GamePacketOut(ServerProt.HEAT_MAP, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
