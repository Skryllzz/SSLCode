package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:41:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class UpdateZoneFullFollows implements GamePacketEncoder {
    private final int chunkX;
    private final int chunkY;

    public UpdateZoneFullFollows(final int chunkX, final int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "X: " + chunkX + ", y: " + chunkY);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.UPDATE_ZONE_FULL_FOLLOWS;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeByte128(chunkX);
        buffer.writeByte(chunkY);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
