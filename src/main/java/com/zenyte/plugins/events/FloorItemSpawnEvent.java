package com.zenyte.plugins.events;

import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.plugins.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Kris | 21/03/2019 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class FloorItemSpawnEvent implements Event {
    private final FloorItem item;

    public FloorItemSpawnEvent(final FloorItem item) {
        this.item = item;
    }

    public FloorItem getItem() {
        return this.item;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof FloorItemSpawnEvent other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$item = this.getItem();
        final Object other$item = other.getItem();
        return Objects.equals(this$item, other$item);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof FloorItemSpawnEvent;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $item = this.getItem();
        result = result * PRIME + ($item == null ? 43 : $item.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "FloorItemSpawnEvent(item=" + this.getItem() + ")";
    }
}
