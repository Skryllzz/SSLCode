package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.EventMouseClickEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;

import java.util.function.Predicate;

/**
 * @author Tommeh | 28 jul. 2018 | 19:55:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class EventMouseClickDecoder implements ClientProtDecoder<EventMouseClickEvent> {
    private static final Predicate<Player> predicate = Player::isNulled;

    @Override
    public EventMouseClickEvent decode(Player player, int opcode, RSBuffer buffer) {
        final int lastPressedBitpack = buffer.readUnsignedShort();
        final int lastButton = lastPressedBitpack & 1;
        final int lastPressedMillis = lastPressedBitpack >>> 1;

        final int lastPressedX = buffer.readUnsignedShort();
        final int lastPressedY = buffer.readUnsignedShort();

        return new EventMouseClickEvent();
    }
}
