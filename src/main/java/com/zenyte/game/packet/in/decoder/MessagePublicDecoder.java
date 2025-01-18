package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.MessagePublicEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;

import static com.zenyte.game.util.huffman.HuffmanManager.readHuffmanString;

/**
 * @author Tommeh | 28 jul. 2018 | 19:25:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MessagePublicDecoder implements ClientProtDecoder<MessagePublicEvent> {
    @Override
    public MessagePublicEvent decode(Player player, int opcode, RSBuffer buffer) {
        final byte type = buffer.readByte();
        final byte colour = buffer.readByte();
        final byte effect = buffer.readByte();
        final String message = readHuffmanString(buffer);
        final byte clanType = type == 3 ? buffer.readByte() : -1;
        return new MessagePublicEvent(type, colour, effect, message, clanType);
    }
}
