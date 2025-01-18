package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 03/03/2019 23:27
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class IfSetScrollPos implements GamePacketEncoder {
    private final int interfaceId;
    private final int componentId;
    private final int height;

    public IfSetScrollPos(final int interfaceId, final int componentId, final int height) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.height = height;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", height: " + height);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.IF_SETSCROLLPOS;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeShortLE(height);
        buffer.writeIntIME(interfaceId << 16 | componentId);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
