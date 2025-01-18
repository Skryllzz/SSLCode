package com.zenyte.network.handshake.codec;

import com.zenyte.network.handshake.packet.inc.HandshakeRequest;
import com.zenyte.network.handshake.packet.inc.HandshakeType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Tommeh | 27 jul. 2018 | 18:50:25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class HandshakeDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        in.markReaderIndex();
        final HandshakeType type = HandshakeType.get(in.readUnsignedByte());
        if (HandshakeType.UPDATE_CONNECTION.equals(type)) {
            if (in.readableBytes() < 4) {
                in.resetReaderIndex();
                return;
            }
            final int revision = in.readInt();
            out.add(new HandshakeRequest(type, revision));
        } else if (HandshakeType.GAME_CONNECTION.equals(type)) {
            out.add(HandshakeType.GAME_CONNECTION);
        } else {
            ctx.close();
        }
    }

    @Override
    public boolean isSingleDecode() {
        return true;
    }
}
