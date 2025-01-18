package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:38:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MidiSong implements GamePacketEncoder {
    private final int song;

    public MidiSong(final int song) {
        this.song = song;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Song: " + song);
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.MIDI_SONG;
        final RSBuffer buffer = new RSBuffer(prot);
        buffer.writeShort(song);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
