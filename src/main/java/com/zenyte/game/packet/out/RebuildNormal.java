package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.XTEALoader;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 13:48:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class RebuildNormal implements GamePacketEncoder {
    private final Player player;

    public RebuildNormal(final Player player) {
        this.player = player;
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Tile: x: " + player.getX() + ", y: " + player.getY() + ", z: " + player.getPlane());
    }

    @Override
    public boolean prioritized() {
        return true;
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.REBUILD_NORMAL;
        final RSBuffer buffer = new RSBuffer(prot);
        player.setForceReloadMap(false);
        final boolean needUpdate = player.isRunning();
        if (!needUpdate) {
            player.getPlayerViewport().init(buffer);
        }
        final Location location = player.getLocation();
        final int chunkX = location.getChunkX();
        final int chunkY = location.getChunkY();
        buffer.writeShortLE(chunkY);
        buffer.writeShortLE128(chunkX);
        final ByteBuf xteasBuf = buffer.alloc().buffer();
        try {
            int regionCount = 0;
            for (int xCalc = (chunkX - 6) / 8; xCalc <= (chunkX + 6) / 8; xCalc++) {
                for (int yCalc = (chunkY - 6) / 8; yCalc <= (chunkY + 6) / 8; yCalc++) {
                    final int regionID = yCalc + (xCalc << 8);
                    for (int xtea : XTEALoader.getXTEAs(regionID)) {
                        xteasBuf.writeInt(xtea);
                    }
                    regionCount++;
                }
            }
            buffer.writeShort(regionCount);
            buffer.writeBytes(xteasBuf);
        } finally {
            xteasBuf.release();
        }
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
