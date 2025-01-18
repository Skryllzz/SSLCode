package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.ChatMessage;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:36:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MessagePrivate implements GamePacketEncoder {
    private final String sender;
    private final ChatMessage message;
    private final int icon;

    public MessagePrivate(final String sender, final ChatMessage message, final int icon) {
        this.sender = sender;
        this.message = message;
        this.icon = icon;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Sender: " + sender + ", icon: " + icon + ", message: " + message.getChatText());
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.MESSAGE_PRIVATE;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeString(sender);
        for (int i = 0; i < 5; i++) {
            buffer.writeByte(Utils.random(255));
        }
        buffer.writeByte(icon);
        buffer.writeBytes(message.getCompressedArray(), 0, message.getOffset());
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
