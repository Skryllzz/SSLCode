package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 31 mrt. 2018 : 22:14:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class IfClearItems implements GamePacketEncoder {
    private final int interfaceId;
    private final int componentId;

    public IfClearItems(final int interfaceId, final int componentId) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.UPDATE_INV_CLEAR;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeIntME(interfaceId << 16 | componentId);
        return new GamePacketOut(ServerProt.UPDATE_INV_CLEAR, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
