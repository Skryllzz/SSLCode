package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:53:08
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MapAnim implements GamePacketEncoder {
    private final Location location;
    private final Graphics graphic;

    public MapAnim(final Location location, final Graphics graphic) {
        this.location = location;
        this.graphic = graphic;
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Graphics: " + graphic.getId() + ", delay: " + graphic.getDelay() + ", height: " + graphic.getHeight() + ", tile: x: " + location.getX() + ", y: " + location.getY() + ", z: " + location.getPlane());
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.MAP_ANIM;
        final RSBuffer buffer = new RSBuffer(prot);
        final int targetLocalX = location.getX() - (location.getChunkX() << 4);
        final int targetLocalY = location.getY() - (location.getChunkY() << 4);
        final int offsetHash = (targetLocalX & 7) << 4 | (targetLocalY & 7);
        buffer.writeByteC(offsetHash);
        buffer.writeShortLE128(graphic.getDelay());
        buffer.write128Byte(graphic.getHeight());
        buffer.writeShort(graphic.getId());
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
