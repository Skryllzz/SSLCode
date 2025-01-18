package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:51:10
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class SetPlayerOp implements GamePacketEncoder {
    private final Player player;
    private final int index;
    private final String option;
    private final boolean top;

    public SetPlayerOp(final Player player, final int index, final String option, final boolean top) {
        this.player = player;
        this.index = index;
        this.option = option;
        this.top = top;
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Index: " + index + ", top: " + top + ", option: " + option);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.SET_PLAYER_OP;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeString(Utils.getOrDefault(option, "null"));
        buffer.writeByteC(top ? 1 : 0);
        buffer.write128Byte(index);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
