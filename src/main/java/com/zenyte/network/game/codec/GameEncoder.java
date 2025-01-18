package com.zenyte.network.game.codec;

import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.security.ISAACCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Tommeh | 28 jul. 2018 | 12:51:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GameEncoder extends MessageToByteEncoder<GamePacketOut> {
    @Override
    protected void encode(ChannelHandlerContext ctx, GamePacketOut packet, ByteBuf out) {
        final ISAACCipher encryptor = ctx.channel().attr(NetworkBootstrap.SESSION).get().getISAACCipherPair().getEncodingRandom();
        final int opcode = packet.getPacket().getOpcode();
        final int size = packet.getPacket().getSize();
        final ByteBuf buffer = packet.getBuffer().resetReaderIndex();
        if (opcode >= 255) {
            final int low = opcode & 255;
            final int high = (opcode >> 8) & 255;
            out.writeByte((high + 128) + encryptor.nextInt());
            out.writeByte((low + encryptor.nextInt()) & 255);
        } else {
            out.writeByte((opcode + encryptor.nextInt()) & 255);
        }
        if (size == -1) {
            out.writeByte(buffer.readableBytes());
        } else if (size == -2) {
            out.writeShort(buffer.readableBytes());
        }
        if (packet.encryptBuffer()) {
            int length = buffer.writerIndex();
            for (int i = buffer.readerIndex(); i < length; i++) {
                out.writeByte((buffer.getByte(i) + encryptor.nextInt()) & 255);
            }
        } else {
            out.writeBytes(buffer);
        }
    }
}
