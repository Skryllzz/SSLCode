package com.zenyte.game.world.entity.player.update.mask;

import com.zenyte.game.world.entity.masks.ChatMessage;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.update.UpdateMask;
import com.zenyte.network.io.RSBuffer;

/**
 * @author Kris | 7. mai 2018 : 17:15:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ChatMask extends UpdateMask {
    @Override
    public UpdateFlag getFlag() {
        return UpdateFlag.CHAT;
    }

    @Override
    public void writePlayer(final RSBuffer buffer, final Player player, final Player processedPlayer) {
        final ChatMessage message = processedPlayer.getChatMessage();

        buffer.writeShortLE(message.getEffects());
        buffer.writeByteC(processedPlayer.getPrimaryIcon());
        buffer.write128Byte((byte) (message.isAutotyper() ? 1 : 0));

        final int offset = message.getOffset();
        buffer.write128Byte(offset);
        buffer.writeBytes(message.getCompressedArray(), 0, offset);
    }
}
