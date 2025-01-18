package com.zenyte.game.world.entity.npc.impl.wilderness.revenants;

import com.zenyte.game.world.entity.masks.Graphics;

/**
 * @author Tommeh | 7 aug. 2018 | 13:20:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum RevenantAttackType {
    MAGIC(1415, new Graphics(-1), new Graphics(1454, 0, 92)),
    RANGED(1452, new Graphics(1451), new Graphics(-1));
    private static final RevenantAttackType[] VALUES = values();
    private final int projectile;
    private final Graphics castGraphics;
    private final Graphics hitGraphics;

    RevenantAttackType(final int projectile, final Graphics castGraphics, final Graphics hitGraphics) {
        this.projectile = projectile;
        this.castGraphics = castGraphics;
        this.hitGraphics = hitGraphics;
    }

    public int getProjectile() {
        return this.projectile;
    }

    public Graphics getCastGraphics() {
        return this.castGraphics;
    }

    public Graphics getHitGraphics() {
        return this.hitGraphics;
    }
}
