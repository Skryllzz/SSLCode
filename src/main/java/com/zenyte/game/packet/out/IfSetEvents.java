package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 16:17:29
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class IfSetEvents implements GamePacketEncoder {
    private final int interfaceId;
    private final int componentId;
    private final int start;
    private final int end;
    private final int set;

    public IfSetEvents(int interfaceId, int componentId, int start, int end, int set) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.start = start;
        this.end = end;
        this.set = set;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", range: " + start + " - " + end + ", mask: " + set);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.IF_SETEVENTS;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeIntME(interfaceId << 16 | componentId);
        buffer.writeShort(end);
        buffer.writeInt(set);
        buffer.writeShortLE(start);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
