package com.zenyte.game.content.theatreofblood.boss.nylocas.model;

import com.zenyte.game.world.entity.Location;

/**
 * @author Tommeh | 6/12/2020 | 10:27 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PillarCorner {
    private final Location primary;
    private final Location secondary;

    public Location getPrimary() {
        return this.primary;
    }

    public Location getSecondary() {
        return this.secondary;
    }

    public PillarCorner(final Location primary, final Location secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }
}
