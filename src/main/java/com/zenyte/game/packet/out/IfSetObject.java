package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:24:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class IfSetObject implements GamePacketEncoder {
    private final int interfaceId;
    private final int componentId;
    private final int itemId;
    private final int zoom;

    public IfSetObject(final int interfaceId, final int componentId, final int itemId, final int zoom) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.itemId = itemId;
        this.zoom = zoom;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", item: " + itemId + ", zoom: " + zoom);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.IF_SETOBJECT;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeShortLE128(itemId);
        buffer.writeIntIME(zoom);
        buffer.writeIntIME(interfaceId << 16 | componentId);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
