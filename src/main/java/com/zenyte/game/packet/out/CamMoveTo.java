package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

public class CamMoveTo implements GamePacketEncoder {
    private final int localX;
    private final int localY;
    private final int cameraHeight;
    private final int speed;
    private final int acceleration;

    public CamMoveTo(final int localX, final int localY, final int cameraHeight, final int speed, final int acceleration) {
        this.localX = localX;
        this.localY = localY;
        this.cameraHeight = cameraHeight;
        this.speed = speed;
        this.acceleration = acceleration;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "X: " + localX + ", y: " + localY + ", height: " + cameraHeight + ", speed: " + speed + ", acceleration: " + acceleration);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.CAM_MOVETO;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeByte(localX);
        buffer.writeByte(localY);
        buffer.writeShort(cameraHeight);
        buffer.writeByte(speed);
        buffer.writeByte(acceleration);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
