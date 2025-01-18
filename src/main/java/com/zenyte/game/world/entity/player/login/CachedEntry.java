package com.zenyte.game.world.entity.player.login;

import com.zenyte.game.world.entity.player.Player;

/**
 * @author Kris | 08/05/2019 01:41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CachedEntry {
    private final long time;
    private final Player cachedAccount;

    public CachedEntry(final long time, final Player cachedAccount) {
        this.time = time;
        this.cachedAccount = cachedAccount;
    }

    public long getTime() {
        return this.time;
    }

    public Player getCachedAccount() {
        return this.cachedAccount;
    }
}
