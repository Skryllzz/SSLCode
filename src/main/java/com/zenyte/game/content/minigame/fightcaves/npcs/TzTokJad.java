package com.zenyte.game.content.minigame.fightcaves.npcs;

import com.zenyte.game.content.minigame.fightcaves.FightCaves;
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
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Emote;

/**
 * @author Kris | 8. nov 2017 : 21:35.50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class TzTokJad extends FightCavesNPC implements CombatScript {
    private static final Animation MELEE_ANIM = new Animation(2655);
    private static final Animation RANGED_ANIM = new Animation(2652);
    private static final Animation MAGIC_ANIM = new Animation(2656);
    private static final Graphics RANGED_GFX = new Graphics(451);
    private static final Graphics MAGIC_GFX = new Graphics(157, 0, 96);
    private static final SoundEffect meleeAttackSound = new SoundEffect(408);
    private static final SoundEffect rangedAttackSound = new SoundEffect(163);
    private static final SoundEffect mageAttackSound = new SoundEffect(162);
    private static final SoundEffect mageLandSound = new SoundEffect(163);
    private static final Projectile MAGIC_PROJ_HEAD = new Projectile(448, 140, 20, 80, 5, 100, 0, 0);
    private static final Projectile MAGIC_PROJ_BODY = new Projectile(449, 140, 20, 85, 5, 100, 0, 0);
    private static final Projectile MAGIC_PROJ_TRAIL = new Projectile(450, 140, 20, 90, 5, 100, 0, 0);
    private static final Projectile[] MAGIC_PROJECTILES = new Projectile[]{MAGIC_PROJ_HEAD, MAGIC_PROJ_BODY, MAGIC_PROJ_TRAIL};
    private final int maximumHealth = getMaxHitpoints() >> 1;
    private boolean spawned;

    TzTokJad(final TzHaarNPC npc, final Location tile, final FightCaves caves) {
        super(npc, tile, caves);
        setAttackDistance(15);
    }

    @Override
    protected void postHitProcess() {
        if (isDead()) return;
        if (!spawned && getHitpoints() < maximumHealth) {
            spawned = true;
            final int size = caves.getMonsters().size() - 1;
            for (int i = 0; i < (4 - size); i++) {
                caves.spawnNPC(TzHaarNPC.YT_HUR_KOT);
            }
        }
    }

    @Override
    public void heal(final int amount) {
        if (isDead() || isFinished()) {
            return;
        }
        super.heal(amount);
        if (getHitpoints() >= getMaxHitpoints()) {
            spawned = false;
        }
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        caves.getPlayer().lock(5);
        caves.getPlayer().setProtectionDelay(Utils.currentTimeMillis() + 5000);
        WorldTasksManager.schedule(() -> caves.getPlayer().setAnimation(Emote.CHEER.getAnimation()), 3);
        caves.finishTracking();
    }

    @Override
    public int attack(Entity target) {
        final int style = Utils.random(isWithinMeleeDistance(this, target) ? 2 : 1);
        if (style == 2) {
            playSound(meleeAttackSound);
            setAnimation(MELEE_ANIM);
            delayHit(0, target, new Hit(this, getRandomMaxHit(this, 97, STAB, target), HitType.MELEE));
        } else if (style == 1) {
            setAnimation(RANGED_ANIM);
            WorldTasksManager.schedule(() -> {
                playSound(rangedAttackSound);
                delayHit(2, target, new Hit(this, getRandomMaxHit(this, 97, RANGED, target), HitType.RANGED));
                target.setGraphics(RANGED_GFX);
                WorldTasksManager.schedule(() -> target.setGraphics(MAGIC_GFX), 1);
            }, 2);
        } else {
            playSound(mageAttackSound);
            setAnimation(MAGIC_ANIM);
            for (final Projectile projectile : MAGIC_PROJECTILES) {
                World.sendProjectile(this, target, projectile);
            }
            WorldTasksManager.schedule(() -> target.setGraphics(MAGIC_GFX), 5);
            WorldTasksManager.schedule(() -> delayHit(3, target, new Hit(this, getRandomMaxHit(this, 97, MAGIC, target), HitType.MAGIC).onLand(h -> playSound(mageLandSound))), 2);
        }
        return combatDefinitions.getAttackSpeed();
    }
}
