package com.zenyte.game.content.treasuretrails.challenges;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Kris | 04/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SongRequest implements ClueChallenge {
    private final String song;

    public SongRequest(final String song) {
        this.song = song;
    }

    public String getSong() {
        return this.song;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof SongRequest other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$song = this.getSong();
        final Object other$song = other.getSong();
        return Objects.equals(this$song, other$song);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof SongRequest;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $song = this.getSong();
        result = result * PRIME + ($song == null ? 43 : $song.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "SongRequest(song=" + this.getSong() + ")";
    }
}
