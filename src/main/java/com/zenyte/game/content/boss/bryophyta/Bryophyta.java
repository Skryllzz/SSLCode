package com.zenyte.game.content.boss.bryophyta;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combat.CombatScript;

/**
 * @author Tommeh | 17/05/2019 | 14:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Bryophyta extends NPC implements CombatScript {
    private static final Animation AUTO_ATTACK_ANIM = new Animation(4658);
    private static final Animation MAGIC_ATTACK_ANIM = new Animation(7173);
    private static final Projectile MAGIC_ATTACK_PROJ = new Projectile(139, 50, 33, 46, 23, -5, 64, 10);
    private static final Graphics MAGIC_ATTACK_ONHIT_GFX = new Graphics(140, 0, 124);
    private static final Graphics SPLASH_GFX = new Graphics(85, 0, 124);
    private static final Graphics GROWTHLING_SPAWN_GFX = new Graphics(86, 0, 100);
    private static final Location[] GROWTHLING_SPAWNS = {new Location(3215, 9936, 0), new Location(3224, 9937, 0), new Location(3221, 9928, 0)};
    private final BryophytaInstance instance;
    private Growthling[] growthlings;

    public Bryophyta(final Location tile, final BryophytaInstance instance) {
        super(8195, tile, Direction.SOUTH, 3);
        this.instance = instance;
        setSpawned(true);
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        if (!growthlingsDead()) {
            hit.setDamage(0);
        }
        super.handleIngoingHit(hit);
    }

    @Override
    public float getXpModifier(final Hit hit) {
        return !growthlingsDead() ? 0 : 1;
    }

    @Override
    public int attack(Entity target) {
        if (isWithinMeleeDistance(this, target)) {
            if (Utils.random(1) == 0) {
                setAnimation(AUTO_ATTACK_ANIM);
                delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 16, MELEE, target), HitType.MELEE));
            } else {
                return distanceAttack(target);
            }
        } else {
            return distanceAttack(target);
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    private int distanceAttack(final Entity target) {
        if (Utils.random(7) == 0 && growthlingsDead()) {
            spawnGrowthlings(target);
        } else {
            setAnimation(MAGIC_ATTACK_ANIM);
            World.sendProjectile(this, target, MAGIC_ATTACK_PROJ);
            delayHit(MAGIC_ATTACK_PROJ.getTime(this, target), target, new Hit(this, getRandomMaxHit(this, 16, MAGIC, target), HitType.MAGIC).onLand(hit -> {
                if (hit.getDamage() == 0) {
                    target.setGraphics(SPLASH_GFX);
                } else {
                    target.setGraphics(MAGIC_ATTACK_ONHIT_GFX);
                }
            }));
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        growthlings = null;
    }

    private void spawnGrowthlings(final Entity target) {
        growthlings = new Growthling[3];
        for (int index = 0; index < 3; index++) {
            final Location location = instance.getLocation(GROWTHLING_SPAWNS[index]);
            growthlings[index] = (Growthling) new Growthling(8194, location).spawn();
            growthlings[index].setGraphics(GROWTHLING_SPAWN_GFX);
            growthlings[index].getCombat().setTarget(target);
        }
    }

    private boolean growthlingsDead() {
        if (growthlings == null) {
            return true;
        }
        for (final Growthling growthling : growthlings) {
            if (growthling == null || !growthling.isFinished()) {
                return false;
            }
        }
        return true;
    }
}
