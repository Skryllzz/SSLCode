package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Kris | 03/03/2019 23:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class UpdateUID192 implements GamePacketEncoder {
    private final byte[] uid;

    public UpdateUID192(final byte[] uid) {
        this.uid = uid;
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "UID: " + Arrays.toString(uid));
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.UPDATE_UID192;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeInt(0);
        buffer.writeBytes(uid);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
