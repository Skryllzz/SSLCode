package com.zenyte.plugins.events;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Kris | 16/09/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class InventoryItemSwitchEvent implements Event {
    private final Player player;
    private final int fromSlot;
    private final int toSlot;

    public InventoryItemSwitchEvent(final Player player, final int fromSlot, final int toSlot) {
        this.player = player;
        this.fromSlot = fromSlot;
        this.toSlot = toSlot;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getFromSlot() {
        return this.fromSlot;
    }

    public int getToSlot() {
        return this.toSlot;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof InventoryItemSwitchEvent other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getFromSlot() != other.getFromSlot()) return false;
        if (this.getToSlot() != other.getToSlot()) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        return Objects.equals(this$player, other$player);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof InventoryItemSwitchEvent;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getFromSlot();
        result = result * PRIME + this.getToSlot();
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "InventoryItemSwitchEvent(player=" + this.getPlayer() + ", fromSlot=" + this.getFromSlot() + ", toSlot=" + this.getToSlot() + ")";
    }
}
