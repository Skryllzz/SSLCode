package com.zenyte.network.handshake.codec;

import com.zenyte.network.handshake.packet.GameRequestHandshakePacketOut;
import com.zenyte.network.handshake.packet.HandshakePacketOut;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Jire
 */
public final class HandshakeEncoder extends MessageToByteEncoder<HandshakePacketOut> {

    @Override
    protected void encode(final ChannelHandlerContext ctx,
                          final HandshakePacketOut packet,
                          final ByteBuf out) {
        out.writeByte(packet.getResponse().getId());

        if (packet instanceof GameRequestHandshakePacketOut gameRequestPacket) {
            out.writeLong(gameRequestPacket.getSessionKey());
        }
    }

}
