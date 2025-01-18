package com.zenyte.game.world.entity.player.action.combat.ranged;

import com.google.common.base.Preconditions;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.RangedCombat;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;

/**
 * @author Kris | 1. juuni 2018 : 00:14:34
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ChinchompaRangedCombat extends RangedCombat {
    private static final int SHORT_FUSE = 0;
    private static final int MEDIUM_FUSE = 1;
    private static final int LONG_FUSE = 3;

    public ChinchompaRangedCombat(final Entity target, final AmmunitionDefinitions defs) {
        super(target, defs);
    }

    @Override
    public int processWithDelay() {
        if (!target.startAttacking(player, CombatType.RANGED)) {
            return -1;
        }
        if (!isWithinAttackDistance()) {
            return 0;
        }
        if (!canAttack()) {
            return -1;
        }
        final Area area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Ranged");
        }
        addAttackedByDelay(player, target);
        final int delayUntilImpact = fireProjectile();
        WorldTasksManager.schedule(() -> target.setGraphics(CombatUtilities.CHINCHOMPA_GFX), delayUntilImpact);
        player.setAnimation(CombatUtilities.CHINCHOMPA_THROW_ANIM);
        final Projectile projectile = this.ammunition.getProjectile();
        final int clientCycles = projectile.getProjectileDuration(player.getLocation(), target.getLocation());
        if (ammunition.getSoundEffect() != null) {
            player.getPacketDispatcher().sendSoundEffect(ammunition.getSoundEffect());
        }
        player.getPacketDispatcher().sendSoundEffect(new SoundEffect(360, 0, clientCycles));
        final float accMod = getAccuracyModifier();
        resetFlag();
        final Hit hit = getHit(player, target, accMod, 1, 1, false);
        delayHit(target, delayUntilImpact, hit);
        final int damage = hit.getDamage();
        if (damage > 0) {
            addPoisonTask(delayUntilImpact);
        }
        attackTarget(getMultiAttackTargets(player), originalTarget -> {
            if (this.target == originalTarget) {
                return true;
            }
            //Chinchompa hits are rolled against the original target's defence.
            final Hit otherHit = getHit(player, originalTarget, accMod, 1, 1, false);
            if (damage == 0) {
                otherHit.setDamage(0);
            }
            delayHit(target, delayUntilImpact, otherHit);
            return true;
        });
        dropAmmunition(delayUntilImpact, true);
        checkIfShouldTerminate();
        return getWeaponSpeed();
    }

    private float getAccuracyModifier() {
        final int distance = (int) player.getLocation().getDistance(target.getLocation());
        final int style = player.getCombatDefinitions().getStyle();
        Preconditions.checkArgument(style == SHORT_FUSE || style == MEDIUM_FUSE || style == LONG_FUSE);
        float accuracyModifier = 1.0F;
        if (style == SHORT_FUSE) {
            if (distance >= 7) {
                accuracyModifier = 0.5F;
            } else if (distance >= 4) {
                accuracyModifier = 0.75F;
            }
        } else if (style == MEDIUM_FUSE) {
            if (distance <= 3 || distance >= 7) {
                accuracyModifier = 0.75F;
            }
        } else {
            if (distance <= 3) {
                accuracyModifier = 0.5F;
            } else if (distance <= 6) {
                accuracyModifier = 0.75F;
            }
        }
        return accuracyModifier;
    }
}
