package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;

/**
 * @author Kris | 18/11/2018 02:52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AttackDefinitions {
    //TODO don't forget to update this on construction.
    private transient AttackType defaultMeleeType = AttackType.CRUSH;
    private AttackType type = AttackType.CRUSH;
    private int maxHit;
    private Animation animation = Animation.STOP;
    private SoundEffect startSound;
    private SoundEffect impactSound;
    private Projectile projectile;
    private Graphics impactGraphics;
    private Graphics drawbackGraphics;

    public static AttackDefinitions construct(final AttackDefinitions clone) {
        final AttackDefinitions defs = new AttackDefinitions();
        if (clone == null) {
            return defs;
        }
        defs.type = clone.type;
        defs.maxHit = clone.maxHit;
        defs.animation = clone.animation;
        defs.startSound = clone.startSound;
        defs.impactSound = clone.impactSound;
        defs.projectile = clone.projectile;
        defs.impactGraphics = clone.impactGraphics;
        defs.drawbackGraphics = clone.drawbackGraphics;
        if (clone.type.isMelee()) {
            defs.defaultMeleeType = clone.type;
        }
        return defs;
    }

    public AttackType getDefaultMeleeType() {
        return this.defaultMeleeType;
    }

    public void setDefaultMeleeType(final AttackType defaultMeleeType) {
        this.defaultMeleeType = defaultMeleeType;
    }

    public AttackType getType() {
        return this.type;
    }

    public void setType(final AttackType type) {
        this.type = type;
    }

    public int getMaxHit() {
        return this.maxHit;
    }

    public void setMaxHit(final int maxHit) {
        this.maxHit = maxHit;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public void setAnimation(final Animation animation) {
        this.animation = animation;
    }

    public SoundEffect getStartSound() {
        return this.startSound;
    }

    public void setStartSound(final SoundEffect startSound) {
        this.startSound = startSound;
    }

    public SoundEffect getImpactSound() {
        return this.impactSound;
    }

    public void setImpactSound(final SoundEffect impactSound) {
        this.impactSound = impactSound;
    }

    public Projectile getProjectile() {
        return this.projectile;
    }

    public void setProjectile(final Projectile projectile) {
        this.projectile = projectile;
    }

    public Graphics getImpactGraphics() {
        return this.impactGraphics;
    }

    public void setImpactGraphics(final Graphics impactGraphics) {
        this.impactGraphics = impactGraphics;
    }

    public Graphics getDrawbackGraphics() {
        return this.drawbackGraphics;
    }

    public void setDrawbackGraphics(final Graphics drawbackGraphics) {
        this.drawbackGraphics = drawbackGraphics;
    }
}
