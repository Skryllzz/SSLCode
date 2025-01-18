package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:28:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class LocAnim implements GamePacketEncoder {
    private final WorldObject object;
    private final Animation animation;

    public LocAnim(final WorldObject object, final Animation animation) {
        this.object = object;
        this.animation = animation;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Id: " + object.getId() + ", type: " + object.getType() + ", rotation: " + object.getRotation() + ", x: " + object.getX() + ", y: " + object.getY() + ", z: " + object.getPlane() + ", animation id: " + animation.getId() + ", delay: " + animation.getDelay());
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.LOC_ANIM;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeShort128(animation.getId());
        buffer.writeByte((object.getType() << 2) | object.getRotation());
        buffer.writeByte((object.getX() & 7) << 4 | (object.getY() & 7));
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
