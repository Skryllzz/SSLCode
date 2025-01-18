package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

public final class MapProjAnimSpecific implements GamePacketEncoder {

    public MapProjAnimSpecific(final Player player, final Position senderTile, final Position receiverObject, final Projectile projectile, final int duration, final int offset) {
    }

    @Override
    public GamePacketOut encode() {
        return new GamePacketOut(ServerProt.PROJANIM_SPECIFIC, new RSBuffer(ServerProt.PROJANIM_SPECIFIC));
    }

    @Override
    public LogLevel level() {
        return LogLevel.SPAM;
    }
}
