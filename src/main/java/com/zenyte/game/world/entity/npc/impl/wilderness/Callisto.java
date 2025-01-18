package com.zenyte.game.world.entity.npc.impl.wilderness;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.player.Player;

import java.util.List;

/**
 * @author Tommeh | 6 feb. 2018 : 20:04:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class Callisto extends NPC implements CombatScript, Spawnable {
    private static final Animation MELEE_ATTACK_ANIM = new Animation(4925);
    private static final Animation KNOCKBACK_ANIM = new Animation(1157);
    private static final Graphics STUN_GFX = new Graphics(254, 0, 92);
    private static final Graphics KNOCKBACK_PLAYER_GFX = new Graphics(1255, 0, 92);
    private static final Graphics KNOCKBACK_NPC_GFX = new Graphics(1257);
    private static final Graphics HEAL_GFX = new Graphics(157);
    private static final Projectile SHOCKWAVE_PROJ = new Projectile(1456, 43, 25, 30, 15, 18, 64, 5);
    private static final Projectile KNOCKBACK_PROJ = new Projectile(1256, 33, 25, 0, 15, 29, 64, 5);

    public Callisto(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public int getRespawnDelay() {
        return 50;
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        final Player player = (Player) hit.getSource();
        if (getTemporaryAttributes().get("CallistoHealingEffect") != null && (boolean) getTemporaryAttributes().get("CallistoHealingEffect")) {
            heal((int) (hit.getDamage() * 0.333));
            setGraphics(HEAL_GFX);
            player.sendMessage("Callisto absorbs his next attack, healing himself a bit.");
            getTemporaryAttributes().remove("CallistoHealingEffect");
        } else {
            super.handleIngoingHit(hit);
        }
    }

    @Override
    public void applyHit(final Hit hit) {
        super.applyHit(hit);
        if (hit.getHitType() == HitType.MAGIC) {
            hit.setDamage(0);
        }
    }

    /**
     * Gets the xp modifier that is used to multiply the combat xp when attacking the monster.
     *
     * @param hit the hit dealt.
     * @return the xp modifier, 0-1.
     */
    @Override
    public float getXpModifier(final Hit hit) {
        return hit.getHitType() == HitType.MAGIC ? 0 : 1;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source instanceof Player player) {
            player.getAchievementDiaries().update(WildernessDiary.KILL_CALLISTO, 1);
        }
    }

    @Override
    public int attack(Entity target) {
        if (target instanceof NPC) {
            return getCombatDefinitions().getAttackSpeed();
        }
        final int attack = Utils.random(combatDefinitions.isMagic() ? 9 : 10);
        final Player player = (Player) target;
        switch (attack) {
            case 0:
                getTemporaryAttributes().put("CallistoHealingEffect", true);
                WorldTasksManager.schedule(() -> getTemporaryAttributes().remove("CallistoHealingEffect"), 3);
                break;
            case 10:
                final Location middle = getMiddleLocation();
                double degrees = Math.toDegrees(Math.atan2(player.getY() - middle.getY(), player.getX() - middle.getX()));
                if (degrees < 0) {
                    degrees += 360;
                }
                final double angle = Math.toRadians(degrees);
                final int px = (int) Math.round(middle.getX() + (getSize() + 4) * Math.cos(angle));
                final int py = (int) Math.round(middle.getY() + (getSize() + 4) * Math.sin(angle));
                final List<Location> tiles = Utils.calculateLine(player.getX(), player.getY(), px, py, player.getPlane());
                if (!tiles.isEmpty()) tiles.remove(0);
                final Location destination = new Location(player.getLocation());
                for (final Location tile : tiles) {
                    final int dir = Utils.getMoveDirection(tile.getX() - destination.getX(), tile.getY() - destination.getY());
                    if (dir == -1) {
                        continue;
                    }
                    if (!World.checkWalkStep(destination.getPlane(), destination.getX(), destination.getY(), dir, player.getSize(), false, false))
                        break;
                    destination.setLocation(tile);
                }
                final int direction = Utils.getFaceDirection(player.getX() - destination.getX(), player.getY() - destination.getY());
                if (!destination.matches(player)) {
                    player.setForceMovement(new ForceMovement(destination, 30, direction));
                    player.lock();
                }
                player.faceEntity(this);
                final Location from = new Location(getLocation().getCoordFaceX(getSize()), getLocation().getCoordFaceY(getSize()), getPlane());
                World.sendProjectile(player, from, KNOCKBACK_PROJ);
                World.sendGraphics(KNOCKBACK_PLAYER_GFX, player.getLocation());
                setAnimation(MELEE_ATTACK_ANIM);
                setGraphics(KNOCKBACK_NPC_GFX);
                player.sendMessage("Callisto's roar throws you backwards.");
                player.setAnimation(KNOCKBACK_ANIM);
                delayHit(this, 0, player, new Hit(this, 3, HitType.REGULAR));
                WorldTasksManager.schedule(() -> {
                    player.setLocation(destination);
                    player.unlock();
                });
                break;
            default:
                setAnimation(MELEE_ATTACK_ANIM);
                if (getCombatDefinitions().isMagic()) {
                    delayHit(this, World.sendProjectile(this, player, SHOCKWAVE_PROJ), player, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MELEE, player), HitType.MELEE).onLand(hit -> {
                        player.setGraphics(STUN_GFX);
                        player.stun(2);
                        player.sendMessage("Callisto's fury sends an almighty shockwave through you.");
                    }));
                } else {
                    delayHit(this, 0, player, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MELEE, player), HitType.MELEE));
                }
                break;
        }
        combatDefinitions.setAttackStyle(Utils.random(5) == 0 ? AttackType.MAGIC : AttackType.MELEE);
        return getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == 6609;
    }
}
