package com.zenyte.game.content.minigame.inferno.npc.impl;

import com.zenyte.game.content.minigame.inferno.instance.Inferno;
import com.zenyte.game.content.minigame.inferno.model.InfernoWave;
import com.zenyte.game.content.minigame.inferno.npc.InfernoNPC;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Tommeh | 29/11/2019 | 19:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class JalTokJad extends InfernoNPC {
    private static final Animation meleeAnimation = new Animation(7590);
    private static final Animation rangedAnimation = new Animation(7593);
    private static final Animation magicAnimation = new Animation(7592);
    private static final Graphics rangedGfx = new Graphics(451);
    private static final Graphics magicGfx = new Graphics(157, 0, 96);
    private static final SoundEffect meleeAttackSound = new SoundEffect(408);
    private static final SoundEffect rangedAttackSound = new SoundEffect(163);
    private static final SoundEffect magicAttackSound = new SoundEffect(162);
    private static final SoundEffect magicLandSound = new SoundEffect(163);
    private static final Projectile magicHeadProj = new Projectile(448, 140, 20, 70, 5, 100, 0, 0);
    private static final Projectile magicBodyProj = new Projectile(449, 140, 20, 75, 5, 100, 0, 0);
    private static final Projectile magicTrailProj = new Projectile(450, 140, 20, 80, 5, 100, 0, 0);
    private static final Projectile[] magicProjectiles = new Projectile[]{magicHeadProj, magicBodyProj, magicTrailProj};
    private static final Location[] wave69HealerLocations = {new Location(2270, 5352, 0), new Location(2270, 5353, 0), new Location(2272, 5352, 0)};
    private final int maximumHealth = getMaxHitpoints() >> 1;
    private final List<YtHurKot> healers = new ArrayList<>(5);

    public JalTokJad(final Location location, final Inferno inferno) {
        super(7700, location, inferno);
        setAttackDistance(15);
    }

    @Override
    protected void postHitProcess() {
        if (isDead()) {
            return;
        }
        if (!spawned && getHitpoints() < maximumHealth) {
            spawned = true;
            final int count = inferno.getNPCs(YtHurKot.class).size();
            final int maxCount = inferno.getWave().equals(InfernoWave.WAVE_67) ? 5 : 3;
            for (int index = 0; index < (maxCount - count); index++) {
                final Location location = inferno.getWave().equals(InfernoWave.WAVE_69) ? inferno.getLocation(wave69HealerLocations[index]) : getHealerLocation();
                final YtHurKot healer = new YtHurKot(location, inferno, this);
                healer.spawn();
                healer.faceEntity(this);
                healers.add(healer);
            }
        }
    }

    private Location getHealerLocation() {
        final Optional<Location> optionalLocation = Utils.findEmptySquare(getLocation(), inferno.getWave().equals(InfernoWave.WAVE_67) ? 9 : 6, 1, Optional.of(l -> {
            final int xOffset = Utils.random(1) == 0 ? Utils.random(2) : Utils.random(7, 9);
            final int yOffset = Utils.random(1) == 0 ? Utils.random(2) : Utils.random(7, 9);
            l.setLocation(l.transform(xOffset, yOffset, 0));
            boolean occupied = false;
            for (final YtHurKot healer : healers) {
                if (healer.getLocation().matches(l)) {
                    occupied = true;
                    break;
                }
            }
            final int distance = l.getTileDistance(getLocation());
            return !occupied && distance >= 4;
        }));
        return optionalLocation.orElseGet(() -> getLocation().transform(Utils.random(3), Utils.random(3), 0));
    }

    @Override
    public boolean isFlinchable() {
        return false;
    }

    @Override
    public void heal(final int amount) {
        super.heal(amount);
        if (getHitpoints() >= getMaxHitpoints()) {
            spawned = false;
        }
    }

    @Override
    public int attack(final Entity target) {
        final int style = Utils.random(isWithinMeleeDistance(this, target) ? 2 : 1);
        if (style == 2) {
            inferno.playSound(meleeAttackSound);
            setAnimation(meleeAnimation);
            delayHit(0, target, new Hit(this, getRandomMaxHit(this, 97, STAB, target), HitType.MELEE));
        } else if (style == 1) {
            setAnimation(rangedAnimation);
            WorldTasksManager.schedule(() -> {
                inferno.playSound(rangedAttackSound);
                delayHit(2, target, new Hit(this, getRandomMaxHit(this, 97, RANGED, target), HitType.RANGED));
                target.setGraphics(rangedGfx);
                WorldTasksManager.schedule(() -> target.setGraphics(magicGfx), 1);
            }, 2);
        } else {
            inferno.playSound(magicAttackSound);
            setAnimation(magicAnimation);
            for (final Projectile projectile : magicProjectiles) {
                World.sendProjectile(this, target, projectile);
            }
            WorldTasksManager.schedule(() -> target.setGraphics(magicGfx), 5);
            WorldTasksManager.schedule(() -> delayHit(3, target, new Hit(this, getRandomMaxHit(this, 97, MAGIC, target), HitType.MAGIC).onLand(h -> inferno.playSound(magicLandSound))), 2);
        }
        return combatDefinitions.getAttackSpeed();
    }

    @Override
    protected void onDeath(Entity source) {
        super.onDeath(source);
        for (final YtHurKot healer : healers) {
            healer.sendDeath();
        }
        healers.clear();
    }
}
