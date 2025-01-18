package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 14. apr 2018 : 14:48.38
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class IfSetAngle implements GamePacketEncoder {
    private final int interfaceId;
    private final int componentId;
    private final int rotationX;
    private final int rotationY;
    private final int modelZoom;

    public IfSetAngle(final int interfaceId, final int componentId, final int rotationX, final int rotationY, final int modelZoom) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.modelZoom = modelZoom;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", rotationX: " + rotationX + ", rotationY: " + rotationY + ", zoom: " + modelZoom);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.IF_SETANGLE;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeShortLE128(rotationX);
        buffer.writeIntLE(interfaceId << 16 | componentId);
        buffer.writeShort128(rotationY);
        buffer.writeShort(modelZoom);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
