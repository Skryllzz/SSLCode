package com.zenyte.game.content.boss.obor;

import com.zenyte.game.CameraShakeType;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;

import java.util.List;

/**
 * @author Tommeh | 14/05/2019 | 10:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Obor extends NPC implements CombatScript {
    private static final Animation AUTO_ATTACK_ANIM = new Animation(4652);
    private static final Animation STAMP_ATTACK_ANIM = new Animation(7183);
    private static final Animation STAMP_ONHIT_ANIM = new Animation(7212);
    private static final Animation KNOCKBACK_ONHIT_ANIM = new Animation(1157);
    private static final Animation PUMMEL_ATTACK_ANIM = new Animation(4666);
    private static final Animation PUMMEL_ONHIT_ANIM = new Animation(7210);
    private static final Graphics STAMP_ATTACK_GFX = new Graphics(140);
    private static final Graphics FALLING_ROCKS_GFX = new Graphics(60, 0, 92);

    public Obor(final Location tile) {
        super(7416, tile, Direction.SOUTH, 3);
        setSpawned(true);
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.5;
    }

    @Override
    public int attack(final Entity target) {
        final int attack = Utils.random(10);
        if (attack <= 2) {
            //stamp attack
            if (!(target instanceof Player player)) {
                return getCombatDefinitions().getAttackSpeed();
            }
            final Location location = new Location(player.getLocation());
            setAnimation(STAMP_ATTACK_ANIM);
            delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 26, RANGED, target), HitType.REGULAR));
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;

                @Override
                public void run() {
                    if (ticks == 0) {
                        //TODO find the correct player anim
                        player.setAnimation(STAMP_ONHIT_ANIM);
                        player.getPacketDispatcher().sendCameraShake(CameraShakeType.UP_AND_DOWN, 20, 5, 0);
                        World.sendGraphics(STAMP_ATTACK_GFX, getFaceLocation(target, 2, 1900));
                        World.sendGraphics(FALLING_ROCKS_GFX, location);
                    } else if (ticks == 1) {
                        player.getPacketDispatcher().resetCamera();
                    }
                    ticks++;
                }
            }, 0, 0);
        } else if (attack == 3) {
            //knockback attack
            final Location location = getLocation();
            double degrees = Math.toDegrees(Math.atan2(target.getY() - location.getY(), location.getX() - location.getX()));
            if (degrees < 0) {
                degrees += 360;
            }
            final double angle = Math.toRadians(degrees);
            final int px = (int) Math.round(location.getX() + (getSize() + 6) * Math.cos(angle));
            final int py = (int) Math.round(location.getY() + (getSize() + 6) * Math.sin(angle));
            final List<Location> tiles = Utils.calculateLine(target.getX(), target.getY(), px, py, target.getPlane());
            if (!tiles.isEmpty()) {
                tiles.remove(0);
            }
            final Location destination = new Location(target.getLocation());
            for (final Location tile : tiles) {
                final int dir = Utils.getMoveDirection(tile.getX() - destination.getX(), tile.getY() - destination.getY());
                if (dir == -1) {
                    continue;
                }
                if (!World.checkWalkStep(destination.getPlane(), destination.getX(), destination.getY(), dir, target.getSize(), false, false))
                    break;
                destination.setLocation(tile);
            }
            final int direction = Utils.getFaceDirection(target.getX() - destination.getX(), target.getY() - destination.getY());
            if (!destination.matches(target)) {
                target.setForceMovement(new ForceMovement(destination, 30, direction));
                target.lock();
            }
            setAnimation(AUTO_ATTACK_ANIM);
            target.setAnimation(KNOCKBACK_ONHIT_ANIM);
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;

                @Override
                public void run() {
                    if (ticks == 0) {
                        if (!destination.matches(target)) {
                            target.setForceMovement(new ForceMovement(destination, 30, direction));
                            target.lock();
                        }
                        target.faceEntity(Obor.this);
                    } else if (ticks == 1) {
                        delayHit(Obor.this, 0, target, new Hit(Obor.this, getRandomMaxHit(Obor.this, 22, MELEE, target), HitType.REGULAR));
                        target.setLocation(destination);
                        target.unlock();
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
        } else if (attack == 4) {
            //pummel attack
            setAnimation(PUMMEL_ATTACK_ANIM);
            WorldTasksManager.schedule(() -> {
                target.stun(2);
                target.setAnimation(PUMMEL_ONHIT_ANIM);
            });
            delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 22, MELEE, target), HitType.MELEE));
        } else {
            //regular auto attack
            setAnimation(AUTO_ATTACK_ANIM);
            delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, 22, MELEE, target), HitType.MELEE));
        }
        return getCombatDefinitions().getAttackSpeed();
    }
}
