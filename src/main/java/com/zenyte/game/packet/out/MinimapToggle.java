package com.zenyte.game.packet.out;

import com.zenyte.game.MinimapState;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:12:09
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MinimapToggle implements GamePacketEncoder {
    private final MinimapState minimapState;

    public MinimapToggle(final MinimapState minimapState) {
        this.minimapState = minimapState;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "State: " + minimapState.name());
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.MINIMAP_TOGGLE;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeByte(minimapState.getState());
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
