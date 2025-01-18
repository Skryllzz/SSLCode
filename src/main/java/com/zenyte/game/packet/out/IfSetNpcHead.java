package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:23:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class IfSetNpcHead implements GamePacketEncoder {
    private final int interfaceId;
    private final int componentId;
    private final int npcId;

    public IfSetNpcHead(final int interfaceId, final int componentId, final int npcId) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.npcId = npcId;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", npc: " + npcId);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.IF_SETNPCHEAD;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeInt(interfaceId << 16 | componentId);
        buffer.writeShortLE128(npcId);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
