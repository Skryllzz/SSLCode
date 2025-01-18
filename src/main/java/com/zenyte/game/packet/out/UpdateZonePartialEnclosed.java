package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerLogger;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 24/10/2018 00:17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class UpdateZonePartialEnclosed implements GamePacketEncoder {
    private static final Class<?>[] ZONE_FOLLOW_TYPES = new Class<?>[]{
            MapProjAnim.class,
            LocCombine.class,
            MapProjAnimSpecific.class,
            ObjDel.class,
            ObjUpdate.class,
            LocAnim.class,
            LocAdd.class,
            LocDel.class,
            MapAnim.class,
            ObjFlags.class,
            ObjAdd.class,
            AreaSound.class,
    };
    private final int x;
    private final int y;
    private final Player player;
    private final List<GamePacketEncoder> packets = new ArrayList<>();

    public UpdateZonePartialEnclosed(final int x, final int y, final Player player) {
        this.x = x;
        this.y = y;
        this.player = player;
    }

    private static int getLocal(int abs, int chunk) {
        return abs - 8 * (chunk - 6);
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "X: " + x + ", y: " + y);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.UPDATE_ZONE_PARTIAL_ENCLOSED;
        final RSBuffer buffer = new RSBuffer(prot);
        final int localX = getLocal(((x >> 3) << 3), player.getLastLoadedMapRegionTile().getChunkX());
        final int localY = getLocal(((y >> 3) << 3), player.getLastLoadedMapRegionTile().getChunkY());
        buffer.writeByteC(localY);
        buffer.writeByteC(localX);
        for (int i = packets.size() - 1; i >= 0; i--) {
            final GamePacketEncoder packet = packets.get(i);
            final int arrayIndex = ArrayUtils.indexOf(ZONE_FOLLOW_TYPES, packet.getClass());
            if (arrayIndex == -1) {
                continue;
            }
            buffer.writeByte(arrayIndex);
            buffer.writeBytes(packet.encode().getBuffer());
            if (packet.level().getPriority() >= PlayerLogger.WRITE_LEVEL.getPriority()) {
                packet.log(player);
            }
        }
        return new GamePacketOut(prot, buffer);
    }

    public void append(final GamePacketEncoder packet) {
        packets.add(packet);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
