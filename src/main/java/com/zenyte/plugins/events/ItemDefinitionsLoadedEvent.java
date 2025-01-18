package com.zenyte.plugins.events;

import com.zenyte.plugins.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kris | 27/07/2019 06:57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ItemDefinitionsLoadedEvent implements Event {
    public ItemDefinitionsLoadedEvent() {
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof ItemDefinitionsLoadedEvent other)) return false;
        return other.canEqual(this);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof ItemDefinitionsLoadedEvent;
    }

    @Override
    public int hashCode() {
        final int result = 1;
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "ItemDefinitionsLoadedEvent()";
    }
}
