package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 19:04:32
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class UpdateStat implements GamePacketEncoder {
    private final int stat;
    private final int experience;
    private final int currentStat;

    public UpdateStat(final int stat, final double experience, final int currentStat) {
        this.stat = stat;
        this.experience = (int) experience;
        this.currentStat = Math.min(255, currentStat);
    }

    public UpdateStat(final int stat, final int experience, final int currentStat) {
        this.stat = stat;
        this.experience = experience;
        this.currentStat = currentStat;
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Stat: " + stat + ", experience: " + player.getSkills().getExperience(stat) + ", current level: " + player.getSkills().getLevel(stat));
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.UPDATE_STAT;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.write128Byte(currentStat);
        buffer.write128Byte(stat);
        buffer.writeIntIME(experience);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
