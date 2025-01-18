package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.util.huffman.HuffmanManager;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:33:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MessageFriendChannel implements GamePacketEncoder {
    private final String channelName;
    private final int icon;

    private final String message;

    private final String senderName;
    private final int message_uid;

    public MessageFriendChannel(Player sender, String channelName, int icon,
                                String message) {
        this.channelName = channelName;
        this.icon = icon;

        this.message = message;

        senderName = sender.getTitleName();
        message_uid = sender.getSocialManager().getNextUniqueId();
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Channel: " + channelName + ", icon: " + icon + ", sender: " + senderName + ", message:" +
                " " + message);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.MESSAGE_FRIENDCHANNEL;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeString(senderName);
        buffer.writeLong(TextUtils.stringToLong(channelName));
        buffer.writeShort(1);
        buffer.write24BitInteger(message_uid);
        buffer.writeByte(0);
        final ByteBuf huffmanBuf = HuffmanManager.encodeHuffmanBuf(message);
        try {
            buffer.writeBytes(huffmanBuf);
        } finally {
            huffmanBuf.release();
        }
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
