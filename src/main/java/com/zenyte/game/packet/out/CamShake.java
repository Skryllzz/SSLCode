package com.zenyte.game.packet.out;

import com.zenyte.game.CameraShakeType;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 4. dets 2017 : 14:09.18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class CamShake implements GamePacketEncoder {
    private final CameraShakeType type;
    private final int shakeIntensity;
    private final int movementIntensity;
    private final int speed;

    public CamShake(final CameraShakeType type, final int shakeIntensity, final int movementIntensity, final int speed) {
        this.type = type;
        this.shakeIntensity = shakeIntensity;
        this.movementIntensity = movementIntensity;
        this.speed = speed;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Type: " + type.name() + ", shake intensity: " + shakeIntensity + ", movement intensity: " + movementIntensity + ", speed: " + speed);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.CAM_SHAKE;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeByte(type.getType());
        buffer.writeByte(shakeIntensity);
        buffer.writeByte(movementIntensity);
        buffer.writeByte(speed);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
