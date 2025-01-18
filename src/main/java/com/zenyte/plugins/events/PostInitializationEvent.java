package com.zenyte.plugins.events;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Corey
 * @since 16/08/19
 */
public class PostInitializationEvent implements Event {
    private final Player player;

    public PostInitializationEvent(final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof PostInitializationEvent other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        return Objects.equals(this$player, other$player);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof PostInitializationEvent;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "PostInitializationEvent(player=" + this.getPlayer() + ")";
    }
}
