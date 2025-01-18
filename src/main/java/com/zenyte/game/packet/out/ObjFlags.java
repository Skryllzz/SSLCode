package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

public final class ObjFlags implements GamePacketEncoder {

    public ObjFlags(final Player player, final Position senderTile, final Position receiverObject, final Projectile projectile, final int duration, final int offset) {
    }

    @Override
    public GamePacketOut encode() {
        return new GamePacketOut(ServerProt.GROUND_OBJECT_OPTION_FLAGS, new RSBuffer(ServerProt.GROUND_OBJECT_OPTION_FLAGS));
    }

    @Override
    public LogLevel level() {
        return LogLevel.SPAM;
    }
}
