package com.zenyte.game.content.boss.kingblackdragon;

import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.Dragonfire;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.DragonfireProtection;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.DragonfireType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;

/**
 * @author Kris | 23. apr 2018 : 15:46.37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class KingBlackDragon extends NPC implements Spawnable, CombatScript {
    private static final Projectile DRAGONFIRE_PROJ = new Projectile(393, 40, 30, 40, 15, 28, 0, 5);
    private static final Projectile POISON_PROJ = new Projectile(394, 40, 30, 40, 15, 28, 0, 5);
    private static final Projectile FREEZING_PROJ = new Projectile(395, 40, 30, 40, 15, 28, 0, 5);
    private static final Projectile SHOCKING_PROJ = new Projectile(396, 40, 30, 40, 15, 28, 0, 5);
    private static final Graphics DRAGONFIRE_GFX = new Graphics(430, 0, 90);
    private static final Graphics POISON_GFX = new Graphics(429, 0, 90);
    private static final Graphics FREEZING_GFX = new Graphics(431, 0, 90);
    private static final Graphics SHOCKING_GFX = new Graphics(428, 0, 90);
    private static final Animation ATTACK_ANIM = new Animation(80);
    private static final Animation SECONDARY_ATTACK_ANIM = new Animation(91);
    private static final Animation DRAGONFIRE_ANIM = new Animation(81);

    public KingBlackDragon(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        this.aggressionDistance = 64;
        this.maxDistance = 64;
        this.attackDistance = 10;
    }

    @Override
    public int getRespawnDelay() {
        return BossRespawnTimer.KING_BLACK_DRAGON.getTimer().intValue();
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return name.equals("king black dragon");
    }

    @Override
    public int attack(final Entity target) {
        if (!(target instanceof Player player)) return 0;
        final KingBlackDragon npc = this;
        final int random = Utils.random(isWithinMeleeDistance(npc, target) ? 2 : 1);
        if (random == 0) {
            npc.setAnimation(DRAGONFIRE_ANIM);
            World.sendProjectile(npc, target, DRAGONFIRE_PROJ);
            final boolean perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
            final double modifier = !perk ? 1 : Math.max(0, Utils.randomDouble() - 0.25F);
            final Dragonfire dragonfire = new Dragonfire(DragonfireType.STRONG_DRAGONFIRE, 65, DragonfireProtection.getProtection(player));
            final int deflected = !perk ? 0 : ((int) Math.floor(dragonfire.getMaximumDamage() * modifier));
            delayHit(npc, DRAGONFIRE_PROJ.getTime(npc, target), target, new Hit(npc, Utils.random(Math.max(0, dragonfire.getDamage() - deflected)), HitType.REGULAR).onLand(hit -> {
                player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's fiery breath"));
                PlayerCombat.appendDragonfireShieldCharges(player);
                target.setGraphics(DRAGONFIRE_GFX);
                if (perk) {
                    dragonfire.backfire(npc, player, 0, deflected);
                }
            }));
        } else if (random == 2) {
            if (Utils.random(1) == 0) {
                npc.setAnimation(ATTACK_ANIM);
            } else {
                npc.setAnimation(SECONDARY_ATTACK_ANIM);
            }
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, 25, MELEE, target), HitType.MELEE));
        } else {
            final int atk = Utils.random(2);
            switch (atk) {
                case 0: {
                    npc.setAnimation(DRAGONFIRE_ANIM);
                    World.sendProjectile(npc, target, POISON_PROJ);
                    final boolean perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
                    final double modifier = !perk ? 1 : Math.max(0, Utils.randomDouble() - 0.25F);
                    final Dragonfire.DragonfireBuilder dragonfire = new Dragonfire.DragonfireBuilder(DragonfireType.STRONG_DRAGONFIRE, 65, DragonfireProtection.getProtection(player)) {
                        @Override
                        public int getDamage() {
                            final float tier = getAccumulativeTier();
                            return tier == 0.0F ? 65 : tier == 0.25F ? 60 : tier == 0.5F ? 35 : tier == 0.75F ? 25 : 10;
                        }
                    };
                    final int deflected = !perk ? 0 : ((int) Math.floor(dragonfire.getMaximumDamage() * modifier));
                    delayHit(npc, POISON_PROJ.getTime(npc, target), target, new Hit(npc, Utils.random(Math.max(0, dragonfire.getDamage() - deflected)), HitType.REGULAR).onLand(hit -> {
                        player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's poisonous breath"));
                        if (Utils.random(3) == 0) {
                            target.getToxins().applyToxin(Toxins.ToxinType.POISON, 8);
                        }
                        if (perk) {
                            dragonfire.backfire(npc, player, 0, deflected);
                        }
                        target.setGraphics(POISON_GFX);
                        PlayerCombat.appendDragonfireShieldCharges(player);
                    }));
                    break;
                }
                case 1: {
                    npc.setAnimation(DRAGONFIRE_ANIM);
                    World.sendProjectile(npc, target, FREEZING_PROJ);
                    final boolean perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
                    final double modifier = !perk ? 1 : Math.max(0, Utils.randomDouble() - 0.25F);
                    final Dragonfire.DragonfireBuilder dragonfire = new Dragonfire.DragonfireBuilder(DragonfireType.STRONG_DRAGONFIRE, 65, DragonfireProtection.getProtection(player)) {
                        @Override
                        public int getDamage() {
                            final float tier = getAccumulativeTier();
                            return tier == 0.0F ? 65 : tier == 0.25F ? 60 : tier == 0.5F ? 35 : tier == 0.75F ? 25 : 10;
                        }
                    };
                    final int deflected = !perk ? 0 : ((int) Math.floor(dragonfire.getMaximumDamage() * modifier));
                    delayHit(npc, FREEZING_PROJ.getTime(npc, target), target, new Hit(npc, Utils.random(Math.max(0, dragonfire.getDamage() - deflected)), HitType.REGULAR).onLand(hit -> {
                        target.setGraphics(FREEZING_GFX);
                        PlayerCombat.appendDragonfireShieldCharges(player);
                        player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's icy breath"));
                        if (perk) {
                            dragonfire.backfire(npc, player, 0, deflected);
                        }
                        if (Utils.random(3) == 0) {
                            player.freeze(16, 0, entity -> player.sendMessage("The dragon's icy attack freezes you."));
                        }
                    }));
                    break;
                }
                case 2: {
                    npc.setAnimation(DRAGONFIRE_ANIM);
                    World.sendProjectile(npc, target, SHOCKING_PROJ);
                    final boolean perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
                    final double modifier = !perk ? 1 : Math.max(0, Utils.randomDouble() - 0.25F);
                    final Dragonfire.DragonfireBuilder dragonfire = new Dragonfire.DragonfireBuilder(DragonfireType.STRONG_DRAGONFIRE, 65, DragonfireProtection.getProtection(player)) {
                        @Override
                        public int getDamage() {
                            final float tier = getAccumulativeTier();
                            return tier == 0.0F ? 65 : tier == 0.25F ? 60 : tier == 0.5F ? 35 : tier == 0.75F ? 25 : 10;
                        }
                    };
                    final int deflected = !perk ? 0 : ((int) Math.floor(dragonfire.getMaximumDamage() * modifier));
                    delayHit(npc, SHOCKING_PROJ.getTime(npc, target), target, new Hit(npc, Utils.random(Math.max(0, dragonfire.getDamage() - deflected)), HitType.REGULAR).onLand(hit -> {
                        target.setGraphics(SHOCKING_GFX);
                        PlayerCombat.appendDragonfireShieldCharges(player);
                        player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's shocking breath"));
                        if (perk) {
                            dragonfire.backfire(npc, player, 0, deflected);
                        }
                        if (Utils.random(3) == 0) {
                            player.getSkills().drainCombatSkills(2);
                            player.sendMessage("The dragon's shocking attack drains your stats.");
                        }
                    }));
                    break;
                }
            }
        }
        return 4;
    }
}
