package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.MovementLock;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 26 mrt. 2018 : 16:55:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class KreeArra extends GodwarsBossNPC implements Spawnable, CombatScript {
    private static final Animation meleeAnimation = new Animation(6981);
    private static final Animation distancedAnimation = new Animation(6980);
    private static final Projectile magicProjectile = new Projectile(1200, 41, 16, 40, 5, 10, 0, 5);
    private static final Projectile rangedProjectile = new Projectile(1199, 41, 16, 40, 5, 10, 0, 5);
    private static final SoundEffect meleeSound = new SoundEffect(3892, 10, 0);
    private static final SoundEffect tornadoSound = new SoundEffect(3870, 10, 0);
    private static final SoundEffect tornadoHitSound = new SoundEffect(2727, 10, -1);
    private static final SoundEffect tornadoSplashSound = new SoundEffect(227, 10, -1);
    private static final Animation knockbackAnimation = new Animation(848);
    private static final Graphics stunGraphics = new Graphics(348, 0, 92);
    long clickDelay;

    public KreeArra(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        if (isAbstractNPC() || tile.getX() >= 6400) return;
        setMinions(new GodwarsBossMinion[]{new WingmanSkree(NpcId.WINGMAN_SKREE, new Location(2834, 5297, 2), Direction.SOUTH, 5), new FlockleaderGeerin(NpcId.FLOCKLEADER_GEERIN, new Location(2827, 5299, 2), Direction.SOUTH, 5), new GodwarsBossMinion(NpcId.FLIGHT_KILISA, new Location(2829, 5300, 2), Direction.SOUTH, 5)});
    }

    public KreeArra(final GodwarsBossMinion[] minions, final int id, final Location tile, final Direction direction, final int radius) {
        this(id, tile, direction, radius);
        setMinions(minions);
    }

    @Override
    BossRespawnTimer timer() {
        return BossRespawnTimer.KREE_ARRA;
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (isForceFollowClose()) {
            if (clickDelay > Utils.currentTimeMillis()) {
                setForceFollowClose(false);
            }
        } else {
            if (clickDelay < Utils.currentTimeMillis()) {
                setForceFollowClose(true);
            }
        }
    }

    @Override
    ForceTalk[] getQuotes() {
        return null;
    }

    @Override
    int diaryFlag() {
        return 4;
    }

    @Override
    public GodType type() {
        return GodType.ARMADYL;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == NpcId.KREEARRA;
    }

    @Override
    public int attack(final Entity target) {
        final KreeArra npc = this;
        if (npc.isForceFollowClose() && Utils.random(1) == 0) {
            final int distanceX = target.getX() - npc.getX();
            final int distanceY = target.getY() - npc.getY();
            final int size = npc.getSize();
            if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
                return 0;
            }
            npc.setAnimation(meleeAnimation);
            World.sendSoundEffect(getMiddleLocation(), meleeSound);
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, 26, MELEE, MAGIC, target), HitType.MELEE));
            return npc.getCombatDefinitions().getAttackSpeed();
        }
        npc.setAnimation(distancedAnimation);
        World.sendSoundEffect(getMiddleLocation(), tornadoSound);
        for (final Entity t : npc.getPossibleTargets(EntityType.PLAYER)) {
            final int style = Utils.random(1);
            if (style == 0) {
                int damage = getRandomMaxHit(npc, 21, MAGIC, RANGED, t);
                //kree'arra deals a minimum of 10 damage upon successful hit; for even distribution, we re-calc it.
                if (damage > 0) {
                    damage = Utils.random(10, 21);
                }
                final Hit hit = new Hit(npc, damage, HitType.MAGIC);
                delayHit(npc, World.sendProjectile(npc, t, magicProjectile), t, hit);
                World.sendSoundEffect(new Location(target.getLocation()), (hit.getDamage() == 0 ? tornadoSplashSound : tornadoHitSound).withDelay(magicProjectile.getProjectileDuration(getMiddleLocation(), target)));
                if (t instanceof Player && Utils.random(2) == 0) {
                    push((Player) t);
                }
            } else {
                final Hit hit = new Hit(npc, getRandomMaxHit(npc, 71, RANGED, t), HitType.RANGED);
                delayHit(npc, World.sendProjectile(npc, t, rangedProjectile), t, hit);
                World.sendSoundEffect(new Location(target.getLocation()), (hit.getDamage() == 0 ? tornadoSplashSound : tornadoHitSound).withDelay(rangedProjectile.getProjectileDuration(getMiddleLocation(), target)));
                if (t instanceof Player && Utils.random(2) == 0) {
                    push((Player) t);
                }
            }
        }
        return npc.getCombatDefinitions().getAttackSpeed();
    }

    private final void push(@NotNull final Player player) {
        final Location tile = player.getFaceLocation(this, 2, 1024);
        final Location destination = new Location(player.getLocation());
        final int dir = Utils.getMoveDirection(tile.getX() - destination.getX(), tile.getY() - destination.getY());
        if (dir != -1) {
            if (World.checkWalkStep(destination.getPlane(), destination.getX(), destination.getY(), dir, player.getSize(), false, false)) {
                destination.setLocation(tile);
            }
        }
        player.faceEntity(this);
        if (!destination.matches(player)) {
            player.setLocation(destination);
        }
        //50% chance to stun regardless if teleported or not.
        if (Utils.random(1) == 0) {
            if (player.getActionManager().getAction() instanceof PlayerCombat && player.getActionManager().getActionDelay() == 0) {
                player.getActionManager().addActionDelay(1);
            }
            player.addMovementLock(new MovementLock(System.currentTimeMillis() + TimeUnit.TICKS.toMillis(1), "You're stunned."));
            player.setAnimation(knockbackAnimation);
            player.setGraphics(stunGraphics);
        }
    }
}
