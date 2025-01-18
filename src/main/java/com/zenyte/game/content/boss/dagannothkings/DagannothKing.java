package com.zenyte.game.content.boss.dagannothkings;

import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 19 mrt. 2018 : 20:39:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public final class DagannothKing extends NPC implements Spawnable, CombatScript {

    private static final Projectile magicProj = new Projectile(162, 63, 25, 27, 15, 33, 64, 5);

    private static final Projectile rangedProj = new Projectile(475, 50, 30, 25, 30, 28, 5, 5);

    public DagannothKing(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
        this.maxDistance = 64;
    }

    @Override
    public int getRespawnDelay() {
        return 150;
    }

    @Override
    public void onDeath(Entity source) {
        super.onDeath(source);
        if (source instanceof Player player) {
            final int flag = id == 2265 ? 1 : id == 2266 ? 2 : 4;
            player.getAchievementDiaries().update(FremennikDiary.KILL_DAGANNOTH_KINGS, flag);
        }
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
    public boolean validate(final int id, final String name) {
        return id >= 2265 && id <= 2267;
    }

    @Override
    public int attack(final Entity target) {
        final DagannothKing npc = this;
        if (getId() == NpcId.DAGANNOTH_PRIME) {
            npc.setAnimation(npc.getCombatDefinitions().getAttackAnim());
            delayHit(npc, World.sendProjectile(npc, target, magicProj), target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), MAGIC, target), HitType.MAGIC));
        } else if (getId() == NpcId.DAGANNOTH_REX) {
            npc.setAnimation(npc.getCombatDefinitions().getAttackAnim());
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), MELEE, target), HitType.MELEE));
        } else {
            npc.setAnimation(npc.getCombatDefinitions().getAttackAnim());
            delayHit(npc, World.sendProjectile(npc, target, rangedProj), target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), RANGED, target), HitType.RANGED));
        }
        return npc.getCombatDefinitions().getAttackSpeed();
    }
}
