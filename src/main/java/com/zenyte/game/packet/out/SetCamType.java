package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 03/03/2019 23:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SetCamType implements GamePacketEncoder {
    private final int type;

    public SetCamType(final int type) {
        this.type = type;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Type: " + type);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.TOGGLE_OCULUS_ORB;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeInt(type);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
