package com.zenyte.plugins.events;

import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Kris | 27/03/2019 13:41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PlayerDeathEvent implements Event {
    @NotNull
    private Player player;
    @Nullable
    private Entity source;

    public PlayerDeathEvent(@NotNull final Player player) {
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @Nullable
    public Entity getSource() {
        return this.source;
    }

    public void setPlayer(@NotNull final Player player) {
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.player = player;
    }

    public void setSource(@Nullable final Entity source) {
        this.source = source;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof PlayerDeathEvent other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (!Objects.equals(this$player, other$player)) return false;
        final Object this$source = this.getSource();
        final Object other$source = other.getSource();
        return Objects.equals(this$source, other$source);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof PlayerDeathEvent;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $source = this.getSource();
        result = result * PRIME + ($source == null ? 43 : $source.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "PlayerDeathEvent(player=" + this.getPlayer() + ", source=" + this.getSource() + ")";
    }
}
