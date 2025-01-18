package com.zenyte.network.handshake;

import com.zenyte.Constants;
import com.zenyte.api.client.query.ApiIPCheck;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import com.zenyte.network.ClientResponse;
import com.zenyte.network.NetworkConstants;
import com.zenyte.network.handshake.codec.HandshakeDecoder;
import com.zenyte.network.handshake.codec.HandshakeEncoder;
import com.zenyte.network.handshake.packet.DefaultHandshakePacketOut;
import com.zenyte.network.handshake.packet.GameRequestHandshakePacketOut;
import com.zenyte.network.handshake.packet.HandshakePacketIn;
import com.zenyte.network.handshake.packet.inc.HandshakeRequest;
import com.zenyte.network.handshake.packet.inc.HandshakeType;
import com.zenyte.network.login.LoginHandler;
import com.zenyte.network.login.codec.LoginDecoder;
import com.zenyte.network.login.codec.LoginEncoder;
import com.zenyte.network.update.UpdateHandler;
import com.zenyte.network.update.codec.UpdateDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.FastThreadLocal;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tommeh | 27 jul. 2018 | 22:14:50
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class HandshakeHandler extends SimpleChannelInboundHandler<HandshakePacketIn> {
    private static final Object2ObjectOpenHashMap<String, List<Channel>> handshakeRequests = new Object2ObjectOpenHashMap<>();
    private static final FastThreadLocal<SecureRandom> RAND = new FastThreadLocal<>() {
        @Override
        protected SecureRandom initialValue() {
            return new SecureRandom();
        }
    };

    private static synchronized boolean verifyConnection(final ChannelHandlerContext ctx) {
        if (Constants.WORLD_PROFILE.isDevelopment()) return true;
        final SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress socketAddress) {
            final String hostAddress = socketAddress.getAddress().getHostAddress();
            if (Constants.ANTIKNOX) {
                if (ApiIPCheck.invalidIPs.contains(hostAddress)) {
                    ctx.close();
                    return false;
                }
            }
            if (PunishmentManager.isPunishmentActive(null, hostAddress, PunishmentType.IP_BAN).isPresent()) {
                ctx.close();
                return false;
            }
            final List<Channel> list = handshakeRequests.computeIfAbsent(hostAddress, k -> new ArrayList<>());
            list.removeIf(channel -> !channel.isOpen());
            if (list.size() >= Constants.MAXIMUM_NUMBER_OF_HANDSHAKE_CONNECTIONS) {
                System.err.println("Too many connections from ip: " + hostAddress);
                ctx.close();
                return false;
            }
            list.add(ctx.channel());
        }
        return true;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HandshakePacketIn msg) {
        if (!verifyConnection(ctx)) {
            return;
        }
        if (msg instanceof HandshakeType type) {
            final long sessionKey = RAND.get().nextLong();
            ctx.write(new GameRequestHandshakePacketOut(type, ClientResponse.SUCCESSFUL, sessionKey), ctx.voidPromise());
            ctx.pipeline().replace(HandshakeDecoder.class.getSimpleName(), LoginDecoder.class.getSimpleName(), new LoginDecoder());
            ctx.pipeline().replace(HandshakeEncoder.class.getSimpleName(), LoginEncoder.class.getSimpleName(), new LoginEncoder());
            ctx.pipeline().replace(HandshakeHandler.class.getSimpleName(), LoginHandler.class.getSimpleName(), new LoginHandler());
            ctx.flush();
        } else if (msg instanceof HandshakeRequest request) {
            if (request.getRevision() == NetworkConstants.REVISION) {
                ctx.write(new DefaultHandshakePacketOut(request.getType(), ClientResponse.SUCCESSFUL), ctx.voidPromise());
                ctx.pipeline().replace(HandshakeDecoder.class.getSimpleName(), UpdateDecoder.class.getSimpleName(), new UpdateDecoder());
                ctx.pipeline().remove(HandshakeEncoder.class.getSimpleName());
                ctx.pipeline().replace(HandshakeHandler.class.getSimpleName(), UpdateHandler.class.getSimpleName(), new UpdateHandler());
                ctx.flush();
            } else {
                ctx.writeAndFlush(new DefaultHandshakePacketOut(request.getType(), ClientResponse.SERVER_UPDATED)).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (cause instanceof IOException) {
            return;
        }
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            ctx.close();
        }
    }
}
