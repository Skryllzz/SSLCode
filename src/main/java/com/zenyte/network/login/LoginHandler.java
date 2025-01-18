package com.zenyte.network.login;

import com.zenyte.game.packet.Session;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.PlayerInformation;
import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.login.packet.LoginPacketIn;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.io.IOException;

/**
 * @author Tommeh | 28 jul. 2018 | 09:35:59
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class LoginHandler extends SimpleChannelInboundHandler<LoginPacketIn> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginPacketIn msg) {
        final Session session = new Session(ctx, msg);
        final PlayerInformation info = new PlayerInformation(session, msg);
        ctx.channel().attr(NetworkBootstrap.SESSION).set(session);
        World.addLoginRequest(info);
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
