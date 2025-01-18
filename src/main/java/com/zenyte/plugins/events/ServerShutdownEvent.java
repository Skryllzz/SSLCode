package com.zenyte.plugins.events;

import com.zenyte.plugins.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kris | 21/03/2019 23:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ServerShutdownEvent implements Event {
    public ServerShutdownEvent() {
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof ServerShutdownEvent other)) return false;
        return other.canEqual(this);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof ServerShutdownEvent;
    }

    @Override
    public int hashCode() {
        final int result = 1;
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "ServerShutdownEvent()";
    }
}
