package com.zenyte.plugins.events;

import com.zenyte.plugins.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kris | 21/03/2019 23:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ServerLaunchEvent implements Event {
    public ServerLaunchEvent() {
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof ServerLaunchEvent other)) return false;
        return other.canEqual(this);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof ServerLaunchEvent;
    }

    @Override
    public int hashCode() {
        final int result = 1;
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "ServerLaunchEvent()";
    }
}
