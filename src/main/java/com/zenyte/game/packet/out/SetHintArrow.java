package com.zenyte.game.packet.out;

import com.zenyte.game.HintArrow;
import com.zenyte.game.HintArrowPosition;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:49:15
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class SetHintArrow implements GamePacketEncoder {
    private final HintArrow icon;

    public SetHintArrow(final HintArrow icon) {
        this.icon = icon;
    }

    @Override
    public void log(@NotNull final Player player) {
        if (icon == null) {
            log(player, "Hint arrow reset");
        } else {
            log(player, "Position: " + icon.getPosition().name() + ", " + (icon.getPosition() == HintArrowPosition.ENTITY ? ("Entity: " + icon.getTarget().getEntityType().name() + ", index: " + icon.getTarget().getIndex()) : ("x: " + icon.getX() + ", y: " + icon.getY() + ", height: " + icon.getHeight())));
        }
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.HINT_ARROW;
        final RSBuffer buffer = new RSBuffer(prot);
        if (icon == null) {
            buffer.writeZero(6);
            return new GamePacketOut(prot, buffer);
        }
        final HintArrowPosition position = icon.getPosition();
        if (HintArrowPosition.ENTITY.equals(position)) {
            final Entity target = icon.getTarget();
            buffer.writeByte(EntityType.NPC.equals(target.getEntityType()) ? 1 : 10);
            buffer.writeShort(target.getIndex());
            buffer.writeZero(3);
        } else {
            buffer.writeByte(position.getPositionHash());
            buffer.writeShort(icon.getX());
            buffer.writeShort(icon.getY());
            buffer.writeByte(icon.getHeight());
        }
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
