package com.zenyte.network;

import com.zenyte.game.packet.Session;
import com.zenyte.network.handshake.HandshakeHandler;
import com.zenyte.network.handshake.codec.HandshakeDecoder;
import com.zenyte.network.handshake.codec.HandshakeEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.incubator.channel.uring.IOUring;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;
import io.netty.incubator.channel.uring.IOUringSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static com.zenyte.Constants.MAX_SERVER_BUFFER_SIZE;

/**
 * @author Tommeh | 27 jul. 2018 | 22:35:25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class NetworkBootstrap {
    public static final AttributeKey<Session> SESSION = AttributeKey.valueOf(Session.class.getSimpleName());
    public static final int IDLE_TIMEOUT_MS = 30000;
    private static final Logger log = LoggerFactory.getLogger(NetworkBootstrap.class);
    private static final ServerBootstrap bootstrap;
    private static final EventLoopGroup boss;
    private static final EventLoopGroup worker;

    static {
        bootstrap = new ServerBootstrap();
        boss = eventLoopGroup(1);
        worker = eventLoopGroup(0);
        bootstrap.group(boss, worker);
        bootstrap.channel(serverSocketChannel(boss));
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, IDLE_TIMEOUT_MS);
        bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(MAX_SERVER_BUFFER_SIZE / 2, MAX_SERVER_BUFFER_SIZE * 2));
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                final ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(IdleStateHandler.class.getSimpleName(), new IdleStateHandler(true, 0, 0, IDLE_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                pipeline.addLast(HandshakeDecoder.class.getSimpleName(), new HandshakeDecoder());
                pipeline.addLast(HandshakeEncoder.class.getSimpleName(), new HandshakeEncoder());
                pipeline.addLast(HandshakeHandler.class.getSimpleName(), new HandshakeHandler());
            }
        });
    }

    public static void bind(final int port) {
        try {
            bootstrap.bind(port).syncUninterruptibly();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public static EventLoopGroup eventLoopGroup(int nThreads) {
        return IOUring.isAvailable() ? new IOUringEventLoopGroup(nThreads) : Epoll.isAvailable() ? new EpollEventLoopGroup(nThreads) : KQueue.isAvailable() ? new KQueueEventLoopGroup(nThreads) : new NioEventLoopGroup(nThreads);
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannel(final EventLoopGroup group) {
        return group instanceof IOUringEventLoopGroup ? IOUringServerSocketChannel.class : group instanceof EpollEventLoopGroup ? EpollServerSocketChannel.class : group instanceof KQueueEventLoopGroup ? KQueueServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends SocketChannel> socketChannel(final EventLoopGroup group) {
        return group instanceof IOUringEventLoopGroup ? IOUringSocketChannel.class : group instanceof EpollEventLoopGroup ? EpollSocketChannel.class : group instanceof KQueueEventLoopGroup ? KQueueSocketChannel.class : NioSocketChannel.class;
    }
}
