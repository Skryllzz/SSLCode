package com.zenyte.game.packet;

import com.zenyte.Constants;
import com.zenyte.Game;
import com.zenyte.game.constants.ClientProt;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerLogger;
import com.zenyte.network.game.packet.GamePacketIn;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.security.ISAACCipherPair;
import com.zenyte.network.login.packet.LoginPacketIn;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jctools.queues.MpscArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Tommeh | 28 jul. 2018 | 11:24:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>
 */
public class Session {
    private static final Logger log = LoggerFactory.getLogger(Session.class);
    private static final ClientProtDecoder<?>[] decoders = Game.getDecoders();
    private final ChannelHandlerContext ctx;
    private final Channel channel;
    private final LoginPacketIn request;
    private final Queue<GamePacketOut> gamePacketOutQueue = new LinkedList<>();
    private final Queue<GamePacketOut> gamePacketOutPrioritizedQueue = new LinkedList<>();
    private final Queue<ClientProtEvent> events = new MpscArrayQueue<>(Constants.CUMULATIVE_PACKETS_LIMIT);
    private final int[] processedPackets = new int[255];
    private final BufferTracker bufferTracker;
    private Player player;
    private volatile boolean closed;

    public Session(final ChannelHandlerContext ctx, final LoginPacketIn request) {
        this.ctx = ctx;
        this.channel = ctx.channel();
        this.request = request;
        this.bufferTracker = new BufferTracker();
    }

    public boolean write(final GamePacketOut packet) {
        if (!bufferTracker.canWrite(packet)) return false;
        if (!channel.isWritable()) return false;
        ctx.write(packet, ctx.voidPromise());
        bufferTracker.appendBytes(packet);
        return true;
    }

    public void flush() {
        ctx.flush();
        bufferTracker.reset();
    }

    public void decode(final GamePacketIn packet) {
        if (closed || player.isNulled()) {
            return;
        }
        player.setLastReceivedPacket(System.currentTimeMillis());
        final int opcode = packet.getOpcode();
        final ClientProt prot = ClientProt.get(opcode);
        try {
            if (opcode < 0 || opcode >= 256) {
                return;
            }
            final ClientProtDecoder<?> decoder = decoders[opcode];
            if (decoder == null) {
                System.err.println("Unhandled opcode: " + opcode);
                return;
            }
            /*if (++processedPackets[opcode] > prot.getLimit()) {
                return;
            }*/
            final ClientProtEvent event = decoder.decode(player, opcode, packet.getBuffer());
            if (event != null) {
                if (!events.offer(event)) {
                    maxPacketLimitExceeded();
                }
            }
        } catch (final Exception e) {
            log.error("", e);
        } finally {
            packet.getBuffer().release();
        }
    }

    private void maxPacketLimitExceeded() throws IllegalStateException {
        closed = true;
        ctx.close();
        throw new IllegalStateException("Maximum packet limit exceeded: " + player.getUsername());
    }

    public void processEvents() {
        for (int i = 0; i < Constants.CUMULATIVE_PACKETS_LIMIT; i++) {
            final ClientProtEvent event = events.poll();
            if (event == null) break;
            try {
                event.handle(player);
                if (event.level().getPriority() >= PlayerLogger.WRITE_LEVEL.getPriority()) {
                    event.log(player);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        Arrays.fill(processedPackets, 0);
    }

    public ISAACCipherPair getISAACCipherPair() {
        return request.getIsaacCipherPair();
    }

    public ChannelHandlerContext getCtx() {
        return this.ctx;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public LoginPacketIn getRequest() {
        return this.request;
    }

    public Queue<GamePacketOut> getGamePacketOutQueue() {
        return this.gamePacketOutQueue;
    }

    public Queue<GamePacketOut> getGamePacketOutPrioritizedQueue() {
        return this.gamePacketOutPrioritizedQueue;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    public BufferTracker getBufferTracker() {
        return this.bufferTracker;
    }
}
