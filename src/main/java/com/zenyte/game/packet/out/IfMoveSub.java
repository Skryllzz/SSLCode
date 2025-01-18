package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 16:09:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class IfMoveSub implements GamePacketEncoder {
    private final int fromPaneId;
    private final int fromPaneChildId;
    private final int toPaneId;
    private final int toPaneChildId;

    public IfMoveSub(final int fromPaneId, final int fromPaneChildId, final int toPaneId, final int toPaneChildId) {
        this.fromPaneId = fromPaneId;
        this.fromPaneChildId = fromPaneChildId;
        this.toPaneId = toPaneId;
        this.toPaneChildId = toPaneChildId;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Pane: " + fromPaneId + " -> " + toPaneId + ", child: " + fromPaneChildId + " -> " + toPaneChildId);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.IF_MOVESUB;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeIntIME(fromPaneId << 16 | fromPaneChildId);
        buffer.writeIntME(toPaneId << 16 | toPaneChildId);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
