package com.zenyte.game.content.boss.grotesqueguardians;

import com.zenyte.utils.Ordinal;

/**
 * @author Tommeh | 01/08/2019 | 21:35
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Ordinal
public enum SphereState {
    LOW(0, 2, 1437),
    MEDIUM(1, 10, 1438),
    HIGH(2, 20, 1439);
    private static final SphereState[] all = values();
    private final int identifier;
    private final int damage;
    private final int projectile;

    SphereState(final int identifier, final int damage, final int projectile) {
        this.identifier = identifier;
        this.damage = damage;
        this.projectile = projectile;
    }

    public SphereState next() {
        return all[ordinal() + 1];
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public int getDamage() {
        return this.damage;
    }

    public int getProjectile() {
        return this.projectile;
    }
}
