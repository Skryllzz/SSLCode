package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:20:18
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class IfSetAnim implements GamePacketEncoder {
    private final int interfaceId;
    private final int componentId;
    private final int animationId;

    public IfSetAnim(final int interfaceId, final int componentId, final int animationId) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.animationId = animationId;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", animation: " + animationId);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.IF_SETANIM;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeShortLE(animationId);
        buffer.writeIntME(interfaceId << 16 | componentId);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
