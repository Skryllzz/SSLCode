package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.info.WorldType;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 03/03/2019 23:18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class LogoutTransfer implements GamePacketEncoder {
    private final WorldType world;

    public LogoutTransfer(final WorldType world) {
        this.world = world;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Address: " + world.getAddress() + ", id: " + world.getWorldId() + ", flag: " + world.getFlag());
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.LOGOUT_TRANSFER;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeString(world.getAddress());
        buffer.writeShort(world.getWorldId());
        buffer.writeInt(world.getFlag());
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
