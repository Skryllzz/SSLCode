package com.zenyte.game.world.entity.player;

import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;

/**
 * @author Kris | 28. juuni 2018 : 20:10:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class AreaManager {
    @SuppressWarnings("unused")
    private final transient Player player;
    private transient Area area;

    public AreaManager(final Player player) {
        this.player = player;
    }

    public boolean sendDeath(final Player player, final Entity source) {
        if (!(area instanceof DeathPlugin)) {
            return false;
        }
        return ((DeathPlugin) area).sendDeath(player, source);
    }

    public Area getArea() {
        return this.area;
    }

    public void setArea(final Area area) {
        this.area = area;
    }
}
